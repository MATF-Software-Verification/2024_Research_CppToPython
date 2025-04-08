#include <iostream>
int main() {
    for (int i = 0; i < 2; ++i) {
        int j = i;
        while (j < 2) {
            if (j % 2 == 0)
                std::cout << i << "," << j << std::endl;
            j++;
        }
    }
    return 0;
}