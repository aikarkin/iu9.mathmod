from functools import reduce
from math import *
from csv import DictReader
from typing import List, Any, Tuple

DATA_DIR = "data/"
DATASET_1 = DATA_DIR + "dataset-metro-1.csv"
DATASET_2 = DATA_DIR + "dataset-metro-2.csv"
DATASET_3 = DATA_DIR + "dataset-metro-3.csv"
TICKET_COSTS = [28, 150, 210]
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
    beta = sqrt(sd_log_x ** 2 / sd_log_y ** 2)
    alpha = exp(avg_log_x - beta * avg_log_y)
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
    csvfile = open(fname, 'r')
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
    th_fields = ["thA0", "thA1", "thB0", "thB1", "thC0", "thC1"]
    r_fields = ["rA0", "rA1", "rB0", "rB1", "rC0", "rC1"]

    dataset = read_dataset(DATASET_1)

    for i in range(len(th_fields)):
        th_values = list(map(lambda el: el[1], dataset[th_fields[i]]))
        r_values = list(map(lambda el: el[1], dataset[r_fields[i]]))
        sample_vec = list(zip(th_values, r_values))
        sample_vec.sort(key=lambda el: el[0])

        xi1 = list(map(lambda t: t[1], sample_vec))
        (alpha, beta) = coefficients(th_values, r_values)
        print("alpha: %.3f, beta: %.3f" % (alpha, beta))
        xi2 = list(map(lambda th: estimated_func(alpha, beta, th), sorted(th_values)))
        print("xi1: %s" % xi1)
        print("xi2: %s" % xi2)
        print("Kolmagorov-Smirnov Test on (xi1, xi2): " + str(two_samples_ks_test(xi1, xi2, ALPHA)))
        print()


def task2():
    th_fields = ["thA0", "thA1", "thB0", "thB1", "thC0", "thC1"]
    ii_fields = ["IIA0", "IIA1", "IIB0", "IIB1", "IIC0", "IIC1"]

    dataset = read_dataset(DATASET_2)

    for i in range(len(th_fields)):
        th_values, r_values = group_dataset_columns(
            dataset[th_fields[i]],
            dataset[ii_fields[i]],
            lambda acc, el: acc + el,
            0
        )
        sample_vec = list(zip(th_values, r_values))
        sample_vec.sort(key=lambda el: el[0])

        xi1 = list(map(lambda t: t[1], sample_vec))
        (alpha, beta) = coefficients(th_values, r_values)
        print("alpha: %.3f, beta: %.3f" % (alpha, beta))
        xi2 = list(map(lambda th: estimated_func(alpha, beta, th), sorted(th_values)))
        print("xi1: %s" % xi1)
        print("xi2: %s" % xi2)
        print("Kolmagorov-Smirnov Test on (xi1, xi2): " + str(two_samples_ks_test(xi1, xi2, ALPHA)))
        print()


def task3():
    ii_fields = ["IIA0", "IIA1", "IIB0", "IIB1", "IIC0", "IIC1"]
    iit_fields = ["IITA0", "IITA1", "IITB0", "IITB1", "IITC0", "IITC1"]

    dataset = read_dataset(DATASET_3)
    for i in range(len(ii_fields)):
        grouped_columns: Tuple[List[int], List[List]] = group_dataset_columns(
            dataset[ii_fields[i]],
            dataset[iit_fields[i]],
            lambda acc, el: acc + [el],
            []
        )
        (ii_values, iit_values) = grouped_columns
        sample_vec: List[Tuple[int, list]] = list(zip(ii_values, iit_values))
        sample_vec.sort(key=lambda el: el[0])
        tickets_count = len(sample_vec[0][1])
        xi3 = list(map(lambda t: t[0], sample_vec))
        for ti in range(tickets_count):
            xi = list(map(lambda t: t[1][ti], sample_vec))
            (alpha, beta) = coefficients(xi3, xi)
            xi_star = list(map(lambda t: estimated_func(alpha, beta, t), xi3))
            print(
                "Kolmagorov-Smirnov Test on {} = psi({}_{}): {}".format(
                    ii_fields[i],
                    iit_fields[i],
                    ti + 1,
                    two_samples_ks_test(xi, xi_star, ALPHA)
                )
            )
            print("alpha=%.3f, beta=%.3f" % (alpha, beta))
            print()
        print('#' * 60)


def task4():
    iit_fields = ["IITA0", "IITA1", "IITB0", "IITB1", "IITC0", "IITC1"]

    dataset = read_dataset(DATASET_3)
    tickets_count = list(
        map(
            lambda i: reduce(
                lambda tt_sum, station: tt_sum + reduce(
                    lambda st_sum, t: st_sum + t[1] if t[0] % 3 == (i + 1) % 3 else st_sum,
                    dataset[station],
                    0
                ),
                iit_fields,
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
    print("avg cost: %.3f" % avg_cost)


def main():
    # task1()
    # task2()
    # task3()
    task4()


if __name__ == '__main__':
    main()
