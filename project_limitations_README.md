# Project Limitations

This project has many limitations based on my inexperience and lack of time when completing it.

When I first started this project, I was only recently starting on Java (and had never really used JavaFx before) so 
a few mistakes were made during the development process.

## Bad Coding Practices

If you look at the source code, there are inconsistent design choices that were used because of my lack of experience
with the object-oriented approach to developing.

This led to:

- **Overuse of static variables/functions:** In many instances, a more sophisticated architecture would have been able to group 
related functions together in such a way that each major Object instance (like OutgoingApiCaller objects) did not have 
to refer to generic, independent static functions and variables which increases memory usage.

- **Mixed naming styles:** This code contains a mix of camel and python text, whereas good coding practices would use
only camel text for Java. This was because I have come from a Python and C-based experience.

- **Overcrowded Controllers:** The Controller Classes may have been overpopulated with code. A more effective approach would
have involved outsourcing some of the code from these Classes to another Class to inherit from or import.

### Lack of Testing

I initially started this project by unit testing some of the static functions, however this was taking me too much time
and was ultimately not required for a hobby project.

Furthermore, no automated integration testing has been attempted, realistically we could have implemented a tool 
like RobotFramework to automate the JavaFx GUI testing.