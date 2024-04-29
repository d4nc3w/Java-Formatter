# Java-Formatter
    [Java Source Code Formatter] [v.1.0] 
Java application combined with HTML functionality, [Spring Web] and [GoogleJavaFormatter] library using [Spring Framework].

Documentation:

    [JavaFormatServic]e
Purpose: Provides a service for formatting Java source code.
Attributes:
formatter: An instance of the Formatter class from the com.google.googlejavaformat.java package. It is initialized in the constructor.
Methods:
format(String code): Takes a string representing Java code as input and returns the formatted version of the code. Throws a FormatterException if any error occurs during formatting.


    [CodeService]
Purpose: Manages code storage and operations, including saving codes with a specified duration and managing expiration of codes.
Attributes:
codeRepository: An instance of CodeRepository that manages the codes.
Methods:
CodeService(): Initializes the service, loading existing codes and starting threads for countdown timers on existing codes.
saveCode(Code code, long duration): Saves a code with the specified duration and starts a countdown thread for the code.
deleteCode(String id): Deletes a code by its ID from the repository.
findCodeById(String id): Retrieves an Optional containing a Code instance if found by ID.
startCountdown(String id): Starts a thread that continuously checks the expiration of a code and deletes it once expired.
convertDuration(long duration, String unit): Converts the duration into seconds based on the specified unit (seconds, minutes, hours, days).
startThreads(Map<String, Code> codes): Starts countdown threads for each code in the provided map.

    [CodeRepository]
Purpose: Manages the storage and retrieval of codes using a HashMap and provides persistence using serialization.
Attributes:
codes: A map containing code objects with their IDs as keys.
Methods:
CodeRepository(): Initializes the repository by loading existing codes from a file.
loadCodes(): Loads existing codes from a file and deletes expired codes.
storeCurrentCodes(): Persists the current codes to a file.
findById(String id): Retrieves an Optional containing a Code instance if found by ID.
saveCode(Code code): Saves a code to the repository and updates the file storage.
delete(String id): Deletes a code by its ID and updates the file storage.
isCodeExpired(Code code): Checks if a given code has expired based on its expiration time.
getCodes(): Retrieves the map of codes.

    [Code]
Purpose: Represents a code entry with an ID, the actual code string, and expiration time.
Attributes:
id: The unique identifier for the code.
code: The actual code string.
time: A Calendar object representing the expiration time of the code.
Constructors:
Code(String id, String code): Initializes a new Code instance with the specified ID and code.
Code(): Default constructor.
Methods:
getId(): Returns the ID of the code.
setId(String id): Sets the ID of the code.
getCode(): Returns the code string.
setCode(String code): Sets the code string.
getTime(): Returns the expiration time.
setTime(Calendar time): Sets the expiration time.
toString(): Returns a string representation of the code.

    [CodeController]
Purpose: Handles HTTP requests related to code formatting and retrieval.
Attributes:
codeService: An instance of CodeService for managing codes.
googleFormatService: An instance of JavaFormatService for formatting Java code.
Constructor:
CodeController(CodeService codeService, JavaFormatService googleFormatService): Initializes the controller with instances of CodeService and JavaFormatService.
Methods:
showForm(Model model): Handles GET requests to show the form for entering new code.
formatCode(Code code, Model model): Handles POST requests to format the code and display the results.
saveCode(Code code, long duration, String unit): Handles POST requests to save a code with the specified duration.
getCode(String id, Model model): Handles GET requests to retrieve a code by its ID and display it.
invalidID(): Returns the view for invalid ID errors.

    [HTML Files]
[codeFormatter.html]:
A form for entering new code and specifying an ID and duration.
Displays the original and formatted code and a form for saving the formatted code.

[formatted.html]:
Displays the formatted code with its ID.
Provides a link to go back and enter a new code.

[formatFailed.html]:
Displays an error message for code formatting failures.
Provides a link to go back and enter a new code.

[invalidID.html]:
Displays an error message for invalid IDs.
Provides a link to go back and enter a new code.
