#include <iostream>

[[maybe_unused]] static void safeAction() noexcept {
    std::cout << "Safe action executed\n";
}

int main() {
    safeAction();
}