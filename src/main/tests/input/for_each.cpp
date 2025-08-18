#include <iostream>
int main() {
    int arr[] = {10, 20, 30};
    for (int i : arr){
        std::cout << i << std::endl;
    }
    return 0;
}