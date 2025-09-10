import typing

def main() -> int:
    arr: list[int] = [1, 2, 3, 4, 5]
    target: int = 5
    left: int = 0
    right: int = len(arr) - 1
    result: int = -1
    while left <= right:
        middle: int = left + (right-left) // 2
        if arr[middle] == target:
            result = middle
            break
        elif arr[middle] < target:
            left = middle + 1
        else:
            right = middle - 1
    if result != -1:
        print("Found at index ", result, sep="")
        print()
    else:
        print("Not found", sep="")
        print()
    return 0

if __name__ == "__main__":
    main()
