package org.example.tpo_07.repository;

import org.example.tpo_07.model.Code;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
