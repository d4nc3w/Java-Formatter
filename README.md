# Java-Formatter
    [Java Source Code Formatter] [v.1.0] 
Java application combined with HTML functionality, [Spring Web] and [GoogleJavaFormatter] library using [Spring Framework].


Documentation:

* [JavaFormatService]

Purpose: Provides a service for formatting Java source code.

Attributes:
formatter: An instance of the Formatter class from the com.google.googlejavaformat.java package. It is initialized in the constructor.
Methods:

format(String code): Takes a string representing Java code as input and returns the formatted version of the code. Throws a FormatterException if any error occurs during formatting.

    @Service
    public class JavaFormatService {
        private Formatter formatter;
    
        public JavaFormatService(){
            formatter = new Formatter();
        }
    
        public String format(String code) throws FormatterException {
            return formatter.formatSource(code);
        }
    }


*  [CodeService]
  
Purpose: Manages code storage and operations, including saving codes with a specified duration and managing expiration of codes.

Attributes:
codeRepository: An instance of CodeRepository that manages the codes.

Methods:
CodeService() -> Initializes the service, loading existing codes and starting threads for countdown timers on existing codes.
saveCode(Code code, long duration) -> Saves a code with the specified duration and starts a countdown thread for the code.
deleteCode(String id) -> Deletes a code by its ID from the repository.
findCodeById(String id) ->  Retrieves an Optional containing a Code instance if found by ID.
startCountdown(String id) -> Starts a thread that continuously checks the expiration of a code and deletes it once expired.
convertDuration(long duration, String unit) -> Converts the duration into seconds based on the specified unit (seconds, minutes, hours, days).
startThreads(Map<String, Code> codes) -> Starts countdown threads for each code in the provided map.

    @Service
    public class CodeService {
        private final CodeRepository codeRepository = new CodeRepository();
    
        public CodeService(){
            Map<String, Code> codes = codeRepository.getCodes();
            if (codes != null && !codes.isEmpty()) {
                startThreads(codes);
            }
        }
    
        public boolean saveCode(Code code, long duration){
            Calendar time = Calendar.getInstance();
            time.add(Calendar.SECOND, (int) duration);
            code.setTime(time);
            if (codeRepository.saveCode(code)) {
                startCountdown(code.getId());
                //startCountdown(code.getId(), duration);
                return true;
            }
            return false;
        }
    
        public void deleteCode(String id){
            codeRepository.delete(id);
            System.out.println("[DELETE] Code with ID: " + id + " expired and has been deleted.");
        }
    
        public Optional<Code> findCodeById(String id) {
            return codeRepository.findById(id);
        }
    
          private void startCountdown(String id) {
            new Thread(
                    () -> {
                      try {
                        while (true) {
                          Thread.sleep(1000);
                          Optional<Code> optionalCode = codeRepository.findById(id);
                          if (optionalCode.isPresent()) {
                            Code code = optionalCode.get();
                            if (codeRepository.isCodeExpired(code)) {
                              deleteCode(id);
                              break;
                            }
                          }
                        }
                      } catch (InterruptedException e) {
                        System.out.println("Thread was interrupted" + e.getMessage());
                      } catch (Exception e) {
                        System.out.println("An error occurred when checking expire time for code" + e.getMessage());
                      }
                    })
                .start();
            }
        
            public long convertDuration(long duration, String unit){
                if(unit.toLowerCase().equals("seconds")){
                    if(duration < 10){
                        System.out.println("Given duration was too short");
                        System.out.println("Duration set to minimum value (10 seconds)");
                        duration = 10;
                    }
                    else if(duration > 7776000){
                        System.out.println("Given duration was too long");
                        System.out.println("Duration set to maximum value (90 days)");
                        duration = 7776000;
                    }
                }
                else if(unit.toLowerCase().equals("minutes")){
                    if(duration * 60 > 7776000){
                        System.out.println("Given duration was too long");
                        System.out.println("Duration set to maximum value (90 days)");
                        duration = 7776000;
                    } else {
                        duration =  duration * 60;
                    }
                }
                else if(unit.toLowerCase().equals("hours")){
                    if(duration * 3600 > 7776000){
                        System.out.println("Given duration was too long");
                        System.out.println("Duration set to maximum value (90 days)");
                        duration = 7776000;
                    } else {
                        duration =  duration * 3600;
                    }
                }
                else if(unit.toLowerCase().equals("days")){
                    if(duration * 86400 > 7776000){
                        System.out.println("Given duration was too long");
                        System.out.println("Duration set to maximum value (90 days)");
                        duration = 7776000;
                    } else {
                        duration = duration * 86400;
                    }
                }
                return duration;
            }
        
            public void startThreads(Map<String, Code> codes) {
                for (Map.Entry<String, Code> entry : codes.entrySet()) {
                    startCountdown(entry.getKey());
                }
            }
        }

* [CodeRepository]
  
Purpose: Manages the storage and retrieval of codes using a HashMap and provides persistence using serialization.

Attributes:
codes: A map containing code objects with their IDs as keys.

Methods:
CodeRepository() -> Initializes the repository by loading existing codes from a file.
loadCodes() -> Loads existing codes from a file and deletes expired codes.
storeCurrentCodes() -> Persists the current codes to a file.
findById(String id)-> Retrieves an Optional containing a Code instance if found by ID.
saveCode(Code code) -> Saves a code to the repository and updates the file storage.
delete(String id) -> Deletes a code by its ID and updates the file storage.
isCodeExpired(Code code) -> Checks if a given code has expired based on its expiration time.
getCodes() -> Retrieves the map of codes.

    public class CodeRepository {
        private Map<String, Code> codes = new HashMap<>();
    
        public CodeRepository() {
            loadCodes();
        }
    
        private void loadCodes(){
            String path = "src/main/resources/database/codes.ser";
            File file = new File(path);
            if (file.exists() && file.length() > 0) {
                try {
                    ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
                    System.out.println("[LOAD] Loading file...");
                    codes = (Map<String, Code>) is.readObject();
    
                    for (Map.Entry<String, Code> entry : codes.entrySet()) {
                        Code code = entry.getValue();
                        if (isCodeExpired(code)) {
                            delete(entry.getKey());
                            System.out.println("[DELETE] Code with ID: " + code.getId() + " expired and has been deleted.");
                        }
                    }
                    System.out.println("Codes loaded successfully");
    
                } catch (IOException e) {
                    System.out.println("Error while loading data from file" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("There are no existing codes to load.");
                System.out.println("[CREATE] Creating file...");
            }
        }
    
        public void storeCurrentCodes(){
            String path = "src/main/resources/database/codes.ser";
            try {
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
                os.writeObject(codes);
                System.out.println("[UPDATE] Codes has been updated and saved to file.");
            } catch (IOException e){
                System.out.println("Error while writing data to file" + e.getMessage());
            }
        }
    
        public Optional<Code> findById(String id) {
            return Optional.ofNullable(codes.get(id));
        }
    
        public boolean saveCode(Code code) {
            if (codes.containsKey(code.getId())) {
                return false;
            }
            codes.put(code.getId(), code);
            storeCurrentCodes();
            return true;
        }
    
        public void delete(String id) {
            codes.remove(id);
            storeCurrentCodes();
        }
    
        public boolean isCodeExpired(Code code){
            Calendar expireTime = code.getTime();
            Calendar currenTime = Calendar.getInstance();
            if(currenTime.after(expireTime)){
                return true;
            } else {
                return false;
            }
        }
    
        public Map<String, Code> getCodes(){
            return codes;
        }
    }

* [Code]
  
Purpose: Represents a code entry with an ID, the actual code string, and expiration time.

Attributes:
id: The unique identifier for the code.
code: The actual code string.
time: A Calendar object representing the expiration time of the code.

Constructors:
Code(String id, String code): Initializes a new Code instance with the specified ID and code.
Code(): Default constructor.

Methods:
getId() -> Returns the ID of the code.
setId(String id) -> Sets the ID of the code.
setCode(String code) -> Sets the code string.
getTime() -> Returns the expiration time.
setTime(Calendar time) -> Sets the expiration time.
toString() -> Returns a string representation of the code.

    public class Code implements Serializable {
    
        private String id;
        private String code;
        private Calendar time;
    
        public Code(String id, String code){
            this.id = id;
            this.code = code;
            this.time = Calendar.getInstance();
        }
    
        public Code(){}
    
        public String getId(){
            return id;
        }
    
        public void setId(String id){
            this.id = id;
        }
    
        public String getCode(){
            return code;
        }
    
        public void setCode(String code) {
            this.code = code;
        }
    
        public Calendar getTime(){
            return time;
        }
    
        public void setTime(Calendar time){
            this.time = time;
        }
    
        @Override
        public String toString() {
            return code;
        }
    }

* [CodeController]
  
Purpose: Handles HTTP requests related to code formatting and retrieval.

Attributes:
codeService: An instance of CodeService for managing codes.
googleFormatService: An instance of JavaFormatService for formatting Java code.

Constructor:
CodeController(...): Initializes the controller with instances of CodeService and JavaFormatService.

Methods:
showForm(Model model) -> Handles GET requests to show the form for entering new code.

formatCode(Code code, Model model) -> Handles POST requests to format the code and display the results.

saveCode(Code code, long duration, ...) -> Handles POST requests to save a code with the specified duration.

getCode(String id, Model model) -> Handles GET requests to retrieve a code by its ID and display it.

invalidID()-> Returns the view for invalid ID errors.

    @Controller
    public class CodeController {
        private final CodeService codeService;
        private final JavaFormatService googleFormatService;
    
        public CodeController(CodeService codeService, JavaFormatService JavaFormatService) {
            this.codeService = codeService;
            this.googleFormatService = JavaFormatService;
        }
    
        @GetMapping("/newCode")
        public String showForm(Model model) {
            model.addAttribute("code", new Code());
            return "codeFormatter";
        }
    
        @PostMapping("/newCode")
        public String formatCode(@ModelAttribute Code code, Model model) {
            model.addAttribute("original", code.getCode());
            try {
                //String formatted = javaFormatService.format(code.getCode());
                //codeService.formatCode(code.getCode()););
                Code fCode = new Code();
                fCode.setId(code.getId());
                fCode.setCode(googleFormatService.format(code.getCode()));
                //javaFormatService.printCodes(code);
                model.addAttribute("formatted", fCode);
            } catch(FormatterException e){
                model.addAttribute("errorMsg", e.getMessage());
                return "formatFailed";
            }
            return "codeFormatter";
        }
    
        @PostMapping("/saveCode")
        public RedirectView saveCode(Code code, @RequestParam long duration, @RequestParam String unit) {
            long convertedDuration = codeService.convertDuration(duration, unit);
            if (codeService.saveCode(code, convertedDuration)) {
                return new RedirectView("/code?id=" + code.getId(), true, false);
            } else {
                return new RedirectView("/invalidID", true, false);
            }
        }
    
        @GetMapping("/code")
        public String getCode(@RequestParam String id, Model model) {
            codeService.findCodeById(id).ifPresent(code -> model.addAttribute("formatted", code));
                return "code";
        }
    
        @GetMapping("/invalidID")
        public String invalidID(){
            return "invalidID";
        }
    }

* [HTML Files]
  
[codeFormatter.html]:

A form for entering new code and specifying an ID and duration.

Displays the original and formatted code and a form for saving the formatted code.

    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Display Code</title>
        <link rel="stylesheet" type="text/css" href="codeFormatter.css">
    </head>
    <body>
    <h1>Enter Code</h1>
    <form method="post" action="/newCode" th:object="${code}">
        <label>
            ID:
            <input th:field="*{id}"/>
        </label>
        <br><br>
        <label>
            Code:
            <textarea th:field="*{code}" rows="10" cols="50"></textarea>
        </label>
        <br><br>
        <button type="submit">Format</button>
    </form>
    
    <table th:if="${original != null}">
        <thead>
        <tr>
            <th>Original</th>
            <th>Formatted</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><pre th:text="${original}"></pre></td>
            <td><pre th:text="${formatted}"></pre></td>
        </tr>
        </tbody>
    </table>
    <form th:if="${original != null}" method="post" action="/saveCode" th:object="${formatted}">
        <input th:field="*{id}" type="hidden" />
        <input th:field="*{code}" type="hidden" />
        <label>Duration:</label>
        <input name="duration" type="number" step="1" required />
        <select id="unit" name="unit" required>
            <option value="seconds">Seconds</option>
            <option value="minutes">Minutes</option>
            <option value="hours">Hours</option>
            <option value="days">Days</option>
        </select>
        <br><br>
        <input type="submit" value="Save this code">
    </form>
    </body>
    </html>

[formatFailed.html]:

Displays an error message for code formatting failures.

Provides a link to go back and enter a new code.

    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Formatting error</title>
        <link rel="stylesheet" type="text/css" href="formatFailed.css">
    </head>
    <body>
        <h1>Program was not able to format given code.</h1>
        <h2>Error message:</h2>
        <h3 th:text="${errorMsg}"></h3>
        <br><br>
        <a href="/newCode">Go back</a>
    </body>
    </html>

[invalidID.html]:

Displays an error message for invalid IDs.

Provides a link to go back and enter a new code.

    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Invalid ID</title>
        <link rel="stylesheet" type="text/css" href="invalidID.css">
    </head>
    <body>
    <h1>Invalid ID</h1>
    <p>A code with this ID already exists.</p>
    <a href="/newCode">Go back</a>
    </body>
    </html>

[code.html]:

Displays the formatted code or a message if there is no code with the given ID.

Provides a link to return to the code entry page.

    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Formatted Code</title>
        <link rel="stylesheet" type="text/css" href="code.css">
    </head>
    <body>
    <div th:if="${formatted}">
        <h1>Given Code After Formatting:</h1>
        <h2 th:text="|ID: ${formatted.id}|"></h2>
        <pre th:utext="${formatted}"></pre>
    </div>
    <h1 th:unless="${formatted}">There is no existing code mapped to the given ID</h1>
    <a href="/newCode">Go back</a>
    </body>
    </html>
