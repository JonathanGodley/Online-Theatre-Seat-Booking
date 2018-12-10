// readme.txt
// program information
//
// Programmer:  Jonathan Godley - c3188072
// Course: SENG2050
// Last modified:  26/03/2018

•	What is the purpose of each of your objects?
      MainPage - displays the booking grid and shows availability and provides a link to the cancel/book page for each seat.
      BookingPage - Takes user input, checks if the seat is still available, and makes the booking by writing to the text file database.
      CancellationPage - Takes user input, checks if the input matches the stored seat booking data, and cancels the booking by re-writing the text file database.
      HtmlGen - A simple valid HTML generator.
•	How do you store each booking?
      I have a 64 line text file that has a line for each seat, maintaining order of seats even when bookings are made and cancelled.
•	The application structure, i.e. relationships among objects etc.
      All three classes call on HtmlGen to create some valid html.
      Mainpage generates hyperlinks to access both the cancellation page and booking page.
      CancellationPage and BookingPage both call their own onPost method when the form created by the doGet method is validly submitted.
•	Identify if you are an MIT student or an undergraduate student.
      Undergraduate
