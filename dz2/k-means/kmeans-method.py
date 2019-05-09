import csv
import random as rnd
from os.path import join
from typing import List

import numpy as np
import plotly.graph_objs as go
import plotly.offline as py

DATA_DIR = "./data"
DATASET_FILE = join(DATA_DIR, "dataset.csv")
HEADERS = ['area', 'RH', 'temp']
CLUSTERS_COUNT = 3
AREA_IDX = 0
RH_IDX = 1
TEMP_IDX = 2
FN_AREA_OF_RH = join(DATA_DIR, "area_of_humidity.html")
FN_AREA_OF_TEMP = join(DATA_DIR, "area_of_temperature.html")
FN_AREA_OF_RH_AND_TEMP = join(DATA_DIR, "area_of_humidity_and_temperature.html")


def read_points_from_csv(filename: str) -> List[np.ndarray]:
    csv_file = open(filename, 'r')
    csv_reader = csv.DictReader(csv_file)
    pts = []
    for row in csv_reader:
        vec = []
        for header in HEADERS:
            vec.append(float(row[header]))
        pts.append(np.array(vec))
    csv_file.close()

    return pts


def draw_clusters_3d(clusters: List[List[np.ndarray]], fname: str) \
        -> None:
    traces = []
    for ci in range(len(clusters)):
        cluster = clusters[ci]
        x_axis = list(map(lambda pt: pt[0], cluster))
        y_axis = list(map(lambda pt: pt[1], cluster))
        z_axis = list(map(lambda pt: pt[2], cluster))

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
                title=HEADERS[0]
            ),
            yaxis=dict(
                title=HEADERS[1]
            ),
            zaxis=dict(
                title=HEADERS[2]
            )
        )
    )

    fig = go.Figure(data=traces, layout=layout)
    py.plot(fig, filename=fname)


def draw_clusters_2d(clusters: List[List[np.ndarray]], fname: str) -> None:
    traces = []

    for ci in range(len(clusters)):
        cluster = clusters[ci]
        x_axis = list(map(lambda pt: pt[0], cluster))
        y_axis = list(map(lambda pt: pt[1], cluster))

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
            title=HEADERS[0],
            zeroline=False
        ),
        yaxis=dict(
            title=HEADERS[1],
            zeroline=False
        )
    )

    fig = go.Figure(data=traces, layout=layout)
    py.plot(fig, filename=fname)


def center_of_mass(pts: List[np.ndarray]) -> np.ndarray:
    mass_ctr = pts[0]
    n = len(pts)

    for i in range(1, n):
        mass_ctr = mass_ctr + pts[i]

    return mass_ctr / n


def kmeans_method(pts: List[np.ndarray], k: int) -> List[List[np.ndarray]]:
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
            ctr[j] = center_of_mass(groups[j])

    return groups


def clusterize_and_draw_2d(pts: List[np.ndarray], xi: int, yi: int, k: int, fname: str) -> None:
    chart_pts = list(map(lambda pt: np.array([pt[xi], pt[yi]]), pts))
    clusters = kmeans_method(chart_pts, k)
    draw_clusters_2d(clusters, fname)


def clusterize_and_draw_3d(pts: List[np.ndarray], xi: int, yi: int, zi: int, k: int, fname: str) -> None:
    chart_pts = list(map(lambda pt: np.array([pt[xi], pt[yi], pt[zi]]), pts))
    clusters = kmeans_method(chart_pts, k)
    draw_clusters_3d(clusters, fname)


def main():
    pts = read_points_from_csv(DATASET_FILE)
    clusterize_and_draw_2d(pts, AREA_IDX, RH_IDX, CLUSTERS_COUNT, FN_AREA_OF_RH)
    clusterize_and_draw_2d(pts, AREA_IDX, TEMP_IDX, CLUSTERS_COUNT, FN_AREA_OF_TEMP)
    clusterize_and_draw_3d(pts, AREA_IDX, RH_IDX, TEMP_IDX, CLUSTERS_COUNT, FN_AREA_OF_RH_AND_TEMP)


if __name__ == '__main__':
    main()
