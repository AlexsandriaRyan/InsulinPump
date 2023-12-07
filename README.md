# InsulinPump
Simulated insulin pump program for Capstone project, winter 2023.

## Known Issues
- the scanner in checkMenu() blocks the UpdateThread. Because of this, the checkMenu() function must be commented out to see recurring updates. The user can complete the input to have the UpdateThread refresh again.
  - I can easily replace this with a BufferedReader and perhaps another thread. Alas, the due date for this project is this week, and the issue is easily bypassable with either the tip above or by commenting out a line or two.
- when saving basal patterns from configs.txt, they are not stored in numerical order. This is natural behaviour of storing an array of hashmaps. That said, the data within each basal pattern remains intact, including whether or not it is the current basal pattern (marked with an '*')

## Presentation
There is a presentation included in this repository that details more about this project. Please view **Insulin Pump Simulation.pdf**

## Video
There is a video that walks through how this project works. [Click Here](https://www.youtube.com/watch?v=E-tawDlWJ8A&ab_channel=AlexRyan)

## Copyright Notice
© 2023 Alexsandria Ryan All rights reserved.
