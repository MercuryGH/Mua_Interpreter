var butFirst = function (lst) {
    return lst.slice(1); // lst[1:]
};
var butLast = function (lst) {
    return lst.slice(0, -1); // lst[:-1]
};
var first = function (lst) {
    return lst[0];
};
var last = function (lst) {
    return lst[lst.length - 1];
};
// ********** 定义库完成 ***********
// 尾递归的reverse()
var reverseItrVer = function (lst, result) {
    if (lst.length == 0) {
        return result;
    }
    return reverseItrVer(butLast(lst), result.concat(last(lst)));
};
reverseItrVer([1, 2, 3], []);
// ********* 阶乘、斐波那契数列
// 可以封装
// 内部函数需要 1 + 1 个参数
var fact = function (n) {
    var tailFact = function (n, result) {
        if (n <= 1)
            return result;
        return tailFact(n - 1, result * n);
    };
    var tailFact2 = function (n, i, result) {
        if (n == i)
            return result;
        return tailFact2(n, i + 1, result * i);
    };
    return tailFact(n, 1) | tailFact2(n, 1, 1);
};
// 内部函数需要1 + 2个参数，因为斐波那契迭代求值需要记忆
var fib = function (n) {
    var tailFib = function (n, cur, next) {
        if (n <= 1)
            return next;
        return tailFib(n - 1, next, cur + next);
    };
    return tailFib(n, 0, 1);
};
// 第二版本的斐波那契数
// 考试写这种比较好
var fib2 = function (n) {
    var tailFib2 = function (n, i, cur, next) {
        if (n == i)
            return cur;
        return tailFib2(n, i + 1, next, cur + next);
    };
    return tailFib2(n, 1, 0, 1);
};
// 高阶函数
// 该函数的用途：对[start, end]内的所有整数的term结果求和
// term(i) 可以是 2 * i，或 i ^ 2，等等
var sum = function (term, start, next, end) {
    if (start > end)
        return 0;
    return term(start) + sum(term, next(start), next, end);
};
sum(function (x) { return x + x; }, 1, function (x) { return x + 1; }, 10); // 110
// 判断素数：i增长版
var isPrime = function (n, i) {
    if (n <= 2)
        return n == 2;
    if (n % i == 0)
        return false;
    if (i * i > n)
        return false;
    return isPrime(n, i + 1);
}; // 使用 isPrime(n, 2) 调用
// 判断素数：i减小版
var isPrime_good = function (n, i) {
    if (i < 2)
        return true;
    if (n % i == 0)
        return false;
    return isPrime_good(n, i - 1);
}; // 使用 isPrime(n, n - 1) 调用
// 快速幂
var pow = function (x, n) {
    if (n == 0)
        return 1;
    if (n % 2 == 0)
        return pow(x, n / 2) * pow(x, n / 2);
    return x * pow(x, n - 1);
};
// 快速幂尾递归
var pow_tail = function (y, x, n) {
    if (n == 0)
        return y;
    if (n == 1)
        return x * y;
    if (n % 2 == 0)
        return pow_tail(y, x * x, n / 2);
    return pow_tail(y * x, x, n - 1);
}; // 使用 pow_tail(1, x, n) 调用
// 快速幂尾递归：版本2
var pow_tail2 = function (x, n, i, j) {
    if (n == 0)
        return 1;
    if (n == 1)
        return i * j; // 重要
    if (n % 2 == 0)
        return pow_tail2(x, n / 2, i * i, j);
    return pow_tail2(x, n - 1, i, i * j);
}; // 使用 pow_tail2(x, n, x, 1) 调用
// i 存储 x^i, j 存储 x^j，最终结果 = x^{i + j}.
// 偶数：i *= i
// 奇数：j *= i
var gcd = function (a, b) {
    if (a % b == 0)
        return b;
    return gcd(b, a % b);
};
/*
make "a 18
make "b 12
make "gcd [[a b] [
    if eq mod :a :b 0
        [return :b]
        [return gcd :b mod :a :b]
]]
print gcd :a :b
*/
// const fermat_test = (n) => {
//     const try_it = (a) => {
//         return expmod(a, n, n) == a;
//     }
//     return try_it(random(n - 1) + 1);
// }
// function expmod(a, n, m) {
//     throw new Error("Function not implemented.");
// }
// function random(arg0: number) {
//     return 1; 
// }
var fTest = function (a) {
    return a;
};
fTest([1, 2, 3]);
