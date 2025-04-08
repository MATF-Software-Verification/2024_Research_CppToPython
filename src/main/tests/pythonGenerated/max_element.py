def findMax(arr,n):
	maxVal = arr
	for i in range(1,n):
		if arr[i]>maxVal:
			maxVal = arr


	return maxVal
def main():
	arr = 10,25,30,42,15
	n = sizeof(arr)/sizeof(arr[0])
	print("Maximum element is " + (findMax(arr,n)) + '\n')
	return 0
