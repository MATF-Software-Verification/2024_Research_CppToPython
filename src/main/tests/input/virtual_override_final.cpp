#include <iostream>

struct Base {
    virtual void doWork() { std::cout << "Base work\n"; }
};

struct Derived : Base {
    void doWork() override final { std::cout << "Derived work (final)\n"; }
};

int main() {
    Derived d;
    d.doWork();
}
