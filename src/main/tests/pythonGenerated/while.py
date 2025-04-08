def main():
	n = 5
	
	left = 0
	right = arr.size()-1
	res = 0
	while left<right:
		middle = (left+right)/2
		if res == arr[middle]:
			break 
		elif arr[middle]<res:
			left = middle+1
		else:
			right = middle-1




	return res
