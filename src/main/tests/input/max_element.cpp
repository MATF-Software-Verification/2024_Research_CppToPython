#include <iostream>
#include <vector>

int findMax(std::vector<int>& arr, int n) {
    int maxVal = arr[0];
    for (int i = 1; i < n; i++) {
        if (arr[i] > maxVal)
            maxVal = arr[i];
    }
    return maxVal;
}

int main() {
    std::vector<int> arr = {10, 25, 30, 42, 15};
    int n = arr.size();
    std::cout << "Maximum element is " << findMax(arr, n) << std::endl;
    return 0;
}
