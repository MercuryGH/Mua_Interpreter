from typing import List

def f(a: int) -> int:
    return a + [1, 2, 3]

print(f([2, 3, 4]))  # MMP CPython对类型根本不敏感
# 要么用mypy检查，要么仰赖IDE吧
# print(f(2))

def g(l: List[int]) -> List[List[List[int]]]:
    return l

print(g([1, 2, 3]))
print(g(0))

test = g(0)
print(test)
