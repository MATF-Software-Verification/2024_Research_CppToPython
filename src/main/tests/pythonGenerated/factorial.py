def factorial(n):
	if n<=1:
		return 1

	return n*(factorial(n-1))
def main():
	num = 5
	res = factorial
	print("Factorial of " + num + " is " + res + '\n')
	return 0
