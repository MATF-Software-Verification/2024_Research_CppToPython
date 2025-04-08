#include <iostream>
#include <vector>


int main(){

    int n = 5;
    std::vector<int> arr(n,0);
    int left = 0;
    int right = arr.size()-1;
    auto res = 0;

    while(left < right){

        int middle = (left + right) / 2;


        if(res == arr[middle]){
            break;
        }
        else if(arr[middle] < res){
            left = middle + 1;
        }else{
            right = middle - 1;
        }

    }

    return res;
}