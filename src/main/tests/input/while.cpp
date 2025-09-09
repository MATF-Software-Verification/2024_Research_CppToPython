#include <iostream>
#include <vector>


int main(){

    int n = 5;
    std::vector<int> arr = {1,2,3,4,5};
    int left = 0;
    int right = arr.size()-1;
    auto res = 5;

    while(left <= right){

        int middle = (left + right) / 2;


        if(res == arr[middle]){
            res = middle;
            break;
        }
        else if(arr[middle] < res){
            left = middle + 1;
        }else{
            right = middle - 1;
        }

    }
    std::cout << res << std::endl;
    return res;
}