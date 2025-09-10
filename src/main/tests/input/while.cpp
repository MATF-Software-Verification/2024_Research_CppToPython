#include <iostream>
#include <vector>

int main() {
    std::vector<int> arr = {1, 2, 3, 4, 5};
    int target = 5;
    int left = 0;
    int right = arr.size() - 1;
    int result = -1;

    while (left <= right) {
        int middle = left + (right - left) / 2;

        if (arr[middle] == target) {
            result = middle;
            break;
        } else if (arr[middle] < target) {
            left = middle + 1;
        } else {
            right = middle - 1;
        }
    }

    if (result != -1)
        std::cout << "Found at index " << result << std::endl;
    else
        std::cout << "Not found" << std::endl;

    return 0;
}
