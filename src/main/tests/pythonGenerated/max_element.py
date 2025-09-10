import typing

def findMax(arr, n) -> int:
    maxVal: int = arr[0]
    for i in range(1, n):
        if arr[i] > maxVal:
            maxVal = arr[i]
    return maxVal

def main() -> int:
    arr: list[int] = [10, 25, 30, 42, 15]
    n: int = len(arr)
    print("Maximum element is ", findMax(arr,n), sep="")
    print()
    return 0

if __name__ == "__main__":
    main()
