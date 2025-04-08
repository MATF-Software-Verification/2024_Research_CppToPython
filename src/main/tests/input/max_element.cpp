#include <iostream>

int findMax(int arr[], int n) {
    int maxVal = arr[0];
    for (int i = 1; i < n; i++) {
        if (arr[i] > maxVal)
            maxVal = arr[i];
    }
    return maxVal;
}

int main() {
    int arr[] = {10, 25, 30, 42, 15};
    int n = sizeof(arr) / sizeof(arr[0]);
    std::cout << "Maximum element is " << (findMax(arr, n)) << std::endl;
    return 0;
}
