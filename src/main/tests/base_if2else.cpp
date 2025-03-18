#include <iostream>
#include <vector>


int main(int argc, char** argv){

    int x = 0;
    int y = 5;

    if(x > 0){
        std::cout << "True"<< std::endl;
    }else if( x-y> 0){
        x = y;
    }else{
        std::cout << "False" << std::endl;
    }

   return 0;
}