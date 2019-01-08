#include <iostream>
#include <pthread.h>
#include <cstdlib> 
#include <ctime>
#include <stdio.h>
#include <stdlib.h>

using namespace std;

int M [2][50];
bool repeat_agency_1, repeat_agency_2;
bool notfull = true;

/**
* This function generates a random number between 1 and 100.
* Main purpose is to create a random seat number for thread 1 & 2.
*/
int random(int n, int m) { return (rand() % (m - n + 1) + n); }

/**
* Seat checker process, initiated by our 'mainthread'.
* Checks if the airplane is empty every time an insertion occurs.
* Temporary boolean value, will become 'false' if function detects an empty seat.
*/
void *seat_check (void *)
{
	bool temp = true;

	for (int a = 0; a < 2; a++)
		for (int b = 0; b < 50; b++)
			if (M [a][b] == 0)
				temp = false;
	/**
	* 'temp' will stay true if and only if there are no empty seats
	* in the airplane, in which case our loop for inserting 1's and 2's
	* will be terminated and the final seat plan will be printed out.
	*/
	if (temp) { notfull = false; }												

	if (false) return (NULL);
}

/**
* Function for filling seats with respect to agency #1.
*/
void *seat_fill_agency1 ( void *seat1)
{
	int * randomseat1 = (int *) seat1;
	
	/**
	* If our random value is even, then it means that the random seat
	* is located at the bottom row (row #2), so we insert accordingly;
	* half of the seat number minus one will give us the column location
	*/
	if (*randomseat1 % 2 == 0)
	{
		if (M[1][((*randomseat1 / 2) - 1)] == 0)
		{
			cout << "Seat number " << *randomseat1 << " is reserved by Agency #" << 1 << endl;
			M[1][((*randomseat1 / 2) - 1)] = 1;
			repeat_agency_1 = false;
		}

		else { repeat_agency_1 = true; }
	}

	/**
	* If our random value is odd, then it means that the random seat
	* is located at the top row (row #1), so we insert accordingly;
	* half of the seat number will give us the column location (decimal will be ignored).
	*/
	else
	{
		if (M[0][(*randomseat1 / 2)] == 0)
		{
			cout << "Seat number " << *randomseat1 << " is reserved by Agency #" << 1 << endl;
			M[0][(*randomseat1 / 2)] = 1;
			repeat_agency_1 = false;
		}

		else { repeat_agency_1 = true; }
	}

	if (false) return (NULL);
}

/**
* Identical copy of 'seat_fill_agency1' function but for 'agency2'.
*/
void *seat_fill_agency2 ( void *seat2)
{
	int * randomseat2 = (int *) seat2;

	if (*randomseat2 % 2 == 0)
	{
		if (M[1][((*randomseat2 / 2) - 1)] == 0)
		{
			cout << "Seat number " << *randomseat2 << " is reserved by Agency #" << 2 << endl;
			M[1][((*randomseat2 / 2) - 1)] = 2;
			repeat_agency_2 = false;
		}

		else { repeat_agency_2 = true; }
	}

	else
	{
		if (M[0][(*randomseat2 / 2)] == 0)
		{
			cout << "Seat number " << *randomseat2 << " is reserved by Agency #" << 2 << endl;
			M[0][(*randomseat2 / 2)] = 2;
			repeat_agency_2 = false;
		}

		else { repeat_agency_2 = true; }
	}

	if (false) return (NULL);
}

int main()
{
	srand(time(NULL)); // Pseudo-random number seed, initiated only once.

	pthread_t mainthread, thread1, thread2; // 'mainthread' for seat check, pthread 1 & 2 for insertion.
	int turn = 0; // Busy waiting algorithm concurrent control value.

	for (int i = 0; i < 2; i++) // We equalize the elements of our globally defined matrix 'M' to zero.
	{
		for (int j = 0; j < 50; j++)
		{
			M [i][j] = 0;
		}
	}

	/**
	* Main loop for insertion process ('notfull' can only become false in seat check).
	*/
	while (notfull)
	{
		int seatnum;
		seatnum = random(1, 100);

		/**
		* 'turn' is initially defined as 0. will become 1 if a successfull insertion occurs.
		*/
		if (turn == 0)
		{
			pthread_create( &thread1, NULL, seat_fill_agency1, (void*) &seatnum);
			pthread_join(thread1, NULL);

			if (repeat_agency_1) { turn = 0; }

			else { turn = 1; }
		}

		else
		{
			pthread_create( &thread2, NULL, seat_fill_agency2, (void*) &seatnum);
			pthread_join(thread2, NULL);

			if (repeat_agency_2) { turn = 1; }

			else { turn = 0; }
		}

		/**
		* Seats are checked at the end of every loop (regardless of a successfull insertion.
		*/
		pthread_create( &mainthread, NULL, seat_check, NULL);
		pthread_join(mainthread, NULL);
	}

	cout << endl;
	cout << endl;

	/**
	* Print the matrix.
	*/
	cout << "------------------------------------------FINAL SEAT PLAN------------------------------------------\n" << endl;

	for (int m = 0; m < 2; m++)
	{
		for (int n = 0; n < 50; n++)
			cout << M [m][n] << " ";

		cout << endl;
	}
	
	cout << endl;
	cout << "---------------------------------------------------------------------------------------------------\n" << endl;

	pthread_exit (NULL);

	cin.ignore();
	cin.get();
	system("PAUSE");
	return 0;
}