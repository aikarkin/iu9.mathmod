import sys
import csv
import random as rnd
import numpy as np
import plotly.offline as py
import plotly.graph_objs as go


def read_points_from_csv(file_name, headers):
    csv_file = open(file_name, 'r')
    csv_reader = csv.DictReader(csv_file)
    pts = []
    for row in csv_reader:
        vec = []
        for header in headers:
            vec.append(float(row[header]))
        pts.append(np.array(vec))
    csv_file.close()

    return pts


def save_clusters(file_name, clusters, headers, main_header_idx):
    csv_file = open(file_name, 'w', newline='\n')
    csv_headers = []
    k = len(clusters)
    headers_len = len(headers)

    csv_headers.append(headers[main_header_idx])

    for hi in range(headers_len):
        if hi is not main_header_idx:
            for ci in range(k):
                csv_headers.append('%s (C#%d)' % (headers[hi], ci + 1))

    writer = csv.DictWriter(csv_file, fieldnames=csv_headers)
    writer.writeheader()

    for ci in range(k):
        row_mapping = {}
        for pt in clusters[ci]:
            for hi in range(headers_len):
                if hi == main_header_idx:
                    row_mapping[headers[hi]] = pt[hi]
                else:
                    row_mapping['%s (C#%d)' % (headers[hi], ci + 1)] = pt[hi]
            writer.writerow(row_mapping)


def draw_clusters_3d(clusters, headers, x_axis_idx, y_axis_idx, z_axis_idx):
    traces = []
    for ci in range(len(clusters)):
        cluster = clusters[ci]
        x_axis = list(map(lambda pt: pt[x_axis_idx], cluster))
        y_axis = list(map(lambda pt: pt[y_axis_idx], cluster))
        z_axis = list(map(lambda pt: pt[z_axis_idx], cluster))

        traces.append(
            go.Scatter3d(
                x=x_axis,
                y=y_axis,
                z=z_axis,
                mode="markers",
                name="Cluster #%d" % (ci + 1),
                marker=dict(
                    size=2,
                    opacity=0.85
                )
            )
        )

    layout = go.Layout(
        title="",
        hovermode="closest",
        legend=dict(
            itemsizing="constant",
        ),
        scene=dict(
            xaxis=dict(
                title=headers[x_axis_idx]
            ),
            yaxis=dict(
                title=headers[y_axis_idx]
            ),
            zaxis=dict(
                title=headers[z_axis_idx]
            )
        )
    )

    fig = go.Figure(data=traces, layout=layout)
    py.plot(fig, filename="clusters-3d")


def draw_clusters(clusters, headers, x_axis_idx, y_axis_idx):
    traces = []

    for ci in range(len(clusters)):
        cluster = clusters[ci]
        x_axis = list(map(lambda pt: pt[x_axis_idx], cluster))
        y_axis = list(map(lambda pt: pt[y_axis_idx], cluster))

        traces.append(
            go.Scatter(
                x=x_axis,
                y=y_axis,
                mode="markers",
                name="Cluster #%d" % (ci + 1)
            )
        )

    layout = go.Layout(
        title="",
        hovermode="closest",
        xaxis=dict(
            title=headers[x_axis_idx],
            zeroline=False
        ),
        yaxis=dict(
            title=headers[y_axis_idx],
            zeroline=False
        )
    )

    fig = go.Figure(data=traces, layout=layout)
    py.plot(fig, filename="basic-scatter")


def find_center_of_mass(pts):
    mass_ctr = pts[0]
    n = len(pts)

    for i in range(1, n):
        mass_ctr = mass_ctr + pts[i]

    return mass_ctr / n


def kmeans_method(pts, k):
    ctr = rnd.sample(pts, k)
    distances = [0.0] * k
    new_distances = [1.0] * k
    groups = [[]] * k

    while new_distances != distances:
        ctr_set = set(map(lambda p: p.tobytes(), ctr))
        distances = new_distances
        new_distances = [set()] * k
        groups = [[] for _ in range(k)]
        for pt in pts:
            if pt.tobytes() not in ctr_set:
                min_dist = 10000.0
                ci = 0
                for j in range(k):
                    dist = np.linalg.norm(ctr[j] - pt)
                    if dist < min_dist:
                        min_dist = dist
                        ci = j
                new_distances[ci].add(min_dist)
                groups[ci].append(pt)

        for j in range(k):
            ctr[j] = find_center_of_mass(groups[j])

    return groups


def main():
    if len(sys.argv) < 3:
        raise Exception("Invalid number of arguments")

    fin = sys.argv[1]
    # fout = sys.argv[2]
    clusters_count = int(sys.argv[2])
    headers = ['area', 'RH', 'temp']
    pts = read_points_from_csv(fin, headers)

    clusters = kmeans_method(pts, clusters_count)
    # save_clusters(fout, clusters, headers, 2)
    draw_clusters_3d(clusters, headers, 0, 1, 2)


if __name__ == '__main__':
    main()
