import matplotlib.pyplot as plt
import pandas as pd

# 从result.csv文件读取数据
df = pd.read_csv('result.csv')

# 定义维度列表
dimensions = ['millis', 'YGC', 'FGC', 'blocked', 'waited']

# 遍历每个维度
for dimension in dimensions:
    plt.figure(figsize=(12, 8))

    # 根据维度分组
    groups = df.groupby('product')

    # 遍历每个产品组
    for name, group in groups:
        plt.plot(group['thread'], group[dimension], marker='o', linestyle='-', label=name)

    plt.xlabel('Thread Count')
    plt.ylabel(dimension)
    plt.title(f'Comparison of {dimension.capitalize()} for Different Products at Different Thread Counts')

    plt.legend()
    plt.grid(True)
    plt.show()