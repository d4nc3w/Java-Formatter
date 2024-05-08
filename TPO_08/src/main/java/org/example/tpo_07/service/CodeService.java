package org.example.tpo_07.service;

import org.example.tpo_07.model.Code;
import org.example.tpo_07.repository.CodeRepository;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Map;
import java.util.Optional;

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
