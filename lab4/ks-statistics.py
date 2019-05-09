from functools import reduce
from math import *
from csv import DictReader
from typing import List, Any, Tuple

DATA_DIR = "data/"
DATASET_1 = DATA_DIR + "dataset-metro-1.csv"
DATASET_2 = DATA_DIR + "dataset-metro-2.csv"
DATASET_3 = DATA_DIR + "dataset-metro-3.csv"

# dataset headers:
R_FIELDS = ["rA0", "rA1", "rB0", "rB1", "rC0", "rC1"]
TH_FIELDS = ["thA0", "thA1", "thB0", "thB1", "thC0", "thC1"]
II_FIELDS = ["IIA0", "IIA1", "IIB0", "IIB1", "IIC0", "IIC1"]
IIT_FIELDS = ["IITA0", "IITA1", "IITB0", "IITB1", "IITC0", "IITC1"]

TICKET_COSTS = [30, 150, 250]
TICKET_NAMES = ["F", "D", "L"]

ALPHA = 0.1


def estimated_func(alpha: float, beta: float, xi: float):
    return alpha * xi ** beta


def average(sample: list):
    return reduce(lambda a, b: a + b, sample) / len(sample)


def standard_deviation(sample: list, sample_avg: float = None):
    if sample_avg is None:
        avg_x = average(sample)
    else:
        avg_x = sample_avg

    dev_sum = reduce(lambda acc, x: acc + (x - avg_x) ** 2.0, sample, 0.0)
    return sqrt(dev_sum / len(sample))


def coefficients(sample_x: list, sample_y: list):
    sample_log_x = list(map(lambda x: log(x), sample_x))
    sample_log_y = list(map(lambda y: log(y), sample_y))
    avg_log_x = average(sample_log_x)
    avg_log_y = average(sample_log_y)
    sd_log_x = standard_deviation(sample_log_x, avg_log_x)
    sd_log_y = standard_deviation(sample_log_y, avg_log_y)
    beta = sqrt(sd_log_y ** 2 / sd_log_x ** 2)
    alpha = exp(avg_log_y - beta * avg_log_x)
    return alpha, beta


def distribution(sample: list):
    n = len(sample)
    values_idxes = [(sample[i], i) for i in range(n)]
    values_idxes.sort(key=lambda el: el[0])
    distr_lst = [0.0] * n
    for i in range(n):
        vi = values_idxes[i][1]
        distr_lst[vi] = (i + 1) / n
    return distr_lst


def ks_statistic(sample_x: list, sample_y: list):
    if len(sample_x) != len(sample_y):
        raise Exception("illegal arguments: lengths of samples must be equal")

    return max(map(lambda el: abs(el[0] - el[1]), zip(distribution(sample_x), distribution(sample_y))))


def two_samples_ks_test(sample_x: list, sample_y: list, approvement_level: float):
    ks_stat = ks_statistic(sample_x, sample_y)
    c = sqrt(-log(approvement_level) / 2.0)
    return ks_stat <= c * sqrt(2.0 / len(sample_x))


def read_dataset(fname):
    csvfile = open(fname, "r")
    reader = list(DictReader(csvfile))
    dataset = {}
    fieldnames = list(next(iter(reader)).keys())

    for key in fieldnames:
        field = key.strip()
        ri = 0

        for row in reader:
            cell_val = row[key].strip()
            if field not in dataset:
                dataset[field] = []

            if len(cell_val) > 0:
                dataset[field].append((ri, float(cell_val)))
            ri += 1

    return dataset


# each column list of type: [(idx_1, value_1), (idx_2, value_2), ...]
def group_dataset_columns(column_x: list, column_y: list, func: callable, initializer: Any) -> Tuple[list, list]:
    n = len(column_x)
    m = len(column_y)
    grouped_y = [0] * n

    for xi in range(n):
        next_xi = column_x[xi + 1][0] if xi < n - 1 else m
        yi = column_y[column_x[xi][0]][0]
        y_val = initializer
        while yi < next_xi:
            y_val = func(y_val, column_y[yi][1])
            yi += 1
        grouped_y[xi] = y_val

    return list(map(lambda t: t[1], column_x)), grouped_y


def task1():
    dataset = read_dataset(DATASET_1)

    th_values = reduce(lambda acc, field: acc + list(map(lambda el: el[1], dataset[field])), TH_FIELDS, [])
    r_values = reduce(lambda acc, field: acc + list(map(lambda el: el[1], dataset[field])), R_FIELDS, [])

    sample_vec = list(zip(r_values, th_values))
    sample_vec.sort(key=lambda el: el[0])
    xi1 = list(map(lambda t: t[1], sample_vec))
    xi2 = list(map(lambda t: t[0], sample_vec))
    (alpha, beta) = coefficients(xi2, xi1)
    xi1_star = list(map(lambda th: estimated_func(alpha, beta, th), sorted(th_values)))
    print("\talpha: %.3f, beta: %.3f" % (alpha, beta))
    print("\tKolmogorov-Smirnov Test on xi1 = f(xi2): %s" % two_samples_ks_test(xi1, xi1_star, ALPHA))


def task2():
    dataset = read_dataset(DATASET_2)

    def aggregate_func(acc: Tuple[list, list], fields: iter) -> Tuple[list, list]:
        grouped_values = group_dataset_columns(
            dataset[fields[1]],
            dataset[fields[0]],
            lambda el_sum, el: el_sum + el,
            0
        )
        return acc[0] + grouped_values[0], acc[1] + grouped_values[1]

    ii_values, th_values = reduce(
        aggregate_func,
        zip(II_FIELDS, TH_FIELDS),
        ([], []),
    )
    sample_vec = list(zip(ii_values, th_values))
    sample_vec.sort(key=lambda el: el[0])

    xi1 = list(map(lambda t: t[1], sample_vec))
    xi3 = list(map(lambda t: t[0], sample_vec))
    (alpha, beta) = coefficients(xi3, xi1)
    xi1_star = list(map(lambda ii: estimated_func(alpha, beta, ii), xi3))
    print("\talpha: %.3f, beta: %.3f" % (alpha, beta))
    print("\tKolmogorov-Smirnov Test on xi1 = f(xi3): " + str(two_samples_ks_test(xi1, xi1_star, ALPHA)))


def task3():
    dataset = read_dataset(DATASET_3)

    def aggregate_func(acc: Tuple[list, list], fields: iter) -> Tuple[list, list]:
        grouped_values = group_dataset_columns(
            dataset[fields[0]],
            dataset[fields[1]],
            lambda ls, el: ls + [el],
            []
        )
        return acc[0] + grouped_values[0], acc[1] + grouped_values[1]

    ii_values, iit_values = reduce(
        aggregate_func,
        zip(II_FIELDS, IIT_FIELDS),
        ([], []),
    )

    y_values = ii_values

    for ti in range(3):
        x_values = list(map(lambda el: el[ti], iit_values))

        sample_vec: List[Tuple[int, list]] = list(zip(x_values, y_values))
        sample_vec.sort(key=lambda el: el[0])

        xik = list(map(lambda t: t[0], sample_vec))
        xi3 = list(map(lambda t: t[1], sample_vec))

        (alpha, beta) = coefficients(xik, xi3)
        xi3_star = list(map(lambda t: estimated_func(alpha, beta, t), xik))

        print("\t-> Ticket %s:" % TICKET_NAMES[ti])
        print("\talpha=%.3f, beta=%.3f" % (alpha, beta))
        print("\tKolmogorov-Smirnov Test on xi3 = f(xi%d): %s" % (ti + 4, two_samples_ks_test(xi3_star, xi3, ALPHA)))
        print("\t" + "-" * 12)


def task4():
    dataset = read_dataset(DATASET_3)
    tickets_count = list(
        map(
            lambda i: reduce(
                lambda tt_sum, station: tt_sum + reduce(
                    lambda st_sum, t: st_sum + t[1] if t[0] % 3 == (i + 1) % 3 else st_sum,
                    dataset[station],
                    0
                ),
                IIT_FIELDS,
                0
            ),
            range(3)
        )
    )
    total_tickets = sum(tickets_count)
    probabilities = list(map(lambda tc: tc / total_tickets, tickets_count))
    avg_cost = reduce(
        lambda s, i: s + TICKET_COSTS[i] * tickets_count[i] * probabilities[i],
        range(3),
        0
    ) / total_tickets
    print("\tavg cost: %.3f" % avg_cost)


def main():
    tasks = [task1, task2, task3, task4]

    for i in range(len(tasks)):
        print("\nTask %d:\n" % (i + 1))
        tasks[i]()
        print("\n" + "#" * 50)


if __name__ == "__main__":
    main()
