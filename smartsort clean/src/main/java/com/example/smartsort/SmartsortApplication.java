package com.example.smartsort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.web.bind.annotation.CrossOrigin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.*;

// TODO: catch errors so that it does not crash
@SpringBootApplication
public class SmartsortApplication {

  public static void main(String[] args) {
      SpringApplication.run(SmartsortApplication.class, args);
  }

  @Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/uploadTest-javaconfig").allowedOrigins("http://localhost:8888");
                registry.addMapping("/upload-javaconfig").allowedOrigins("http://localhost:8888");
                registry.addMapping("/uploadClean-javaconfig").allowedOrigins("http://localhost:8888");
                registry.addMapping("/createAccount-javaconfig").allowedOrigins("http://localhost:8888");
                registry.addMapping("/login-javaconfig").allowedOrigins("http://localhost:8888");
                registry.addMapping("/verifyLogin-javaconfig").allowedOrigins("http://localhost:8888");
                registry.addMapping("/signout-javaconfig").allowedOrigins("http://localhost:8888");
                registry.addMapping("/deleteAccount-javaconfig").allowedOrigins("http://localhost:8888");
                registry.addMapping("/createForm-javaconfig").allowedOrigins("http://localhost:8888");
                registry.addMapping("/getMyForms-javaconfig").allowedOrigins("http://localhost:8888");
                //registry.addMapping("/deleteForm-javaconfig").allowedOrigins("http://localhost:8888");
			}
		};
	}

}

@RestController
class FormDataController {

  @CrossOrigin(origins = "http://localhost:8888")
  @GetMapping("/uploadTest")
  public String testUpload() {
    return "Upload connected.";
  }

  @CrossOrigin(origins = "http://localhost:8888")
  @PostMapping("/upload")
  public String handleFormUpload(@ModelAttribute SortInput sortInput, @RequestParam String type) {

      if (! AccountServices.verifyLogin(sortInput.getEmail(), sortInput.getKey()))
          return "{\"text\":\"INVALID\"}";

      Sort bestSort = null;
      ArrayList<Double> bestAverageUnhappinessOverIterations = null;
      for (int sortNum = 0; sortNum < sortInput.getNumber3(); sortNum++) {
          double[][] weights = {{sortInput.getWeight1()}, {sortInput.getWeight2()}, {sortInput.getWeight3()}, {sortInput.getWeight4()}, {sortInput.getWeight5()}, {sortInput.getWeight6()}, {sortInput.getWeight7()}, {sortInput.getWeight8()}, sortInput.getWeight9(), sortInput.getWeight10(), {sortInput.getNumber1()}, sortInput.getWeight11(), sortInput.getWeight12()};
          ArrayList<ArrayList<String>> input = MyUtility.readJsonStringInput(sortInput.getFile1());
          Individual[] ind = new Individual[input.size()];
          for (int i = 0; i < input.size(); i++)
              ind[i] = new Individual(input.get(i), new int[] {sortInput.getValue1(), sortInput.getValue2(), sortInput.getValue3(), sortInput.getValue4(), sortInput.getValue5(), sortInput.getValue6(), sortInput.getValue7(), sortInput.getValue8(), sortInput.getValue9(), sortInput.getValue10(), sortInput.getValue11(), sortInput.getValue12()}, weights);
              ArrayList<ArrayList<String>> input1 = MyUtility.readJsonStringInput(sortInput.getFile2());
          Location[] l = new Location[input1.size()];
          for (int i = 0; i < input1.size(); i++)
              l[i] = new Location(Integer.parseInt(input1.get(i).get(1)), input1.get(i).get(0), Integer.parseInt(input1.get(i).get(2)), weights);
          Sort sort = new Sort(l, ind);
          for (int i = 0; i < input1.size(); i++)
              l[i].calculateMaxUnhappiness(sort);

          int iter = sortInput.getNumber2();
          if (iter > 1000000) {
              iter = 1000000;
          }
          ArrayList<Double> averageUnhappinessOverIterations = new ArrayList<>();

        if (type.equals("normal")) {
              sort.allChoiceAssignment();

              // TODO: Confirm that iteration actually helps
              for (int i = 0; i < iter; i++) {
                  // TODO: What is the best setting for reassignX?
                  sort.reassignX(3);
                  sort.sumUnhappiness();
                  averageUnhappinessOverIterations.add(sort.getUnhappiness()/sort.getIndividuals().length);
                  //System.out.print(sort.getUnhappiness() + ",");
              }
          } else {
              sort.randomAssignment();
              sort.sumUnhappiness();
          }
          if (bestSort == null || bestSort.getUnhappiness() > sort.getUnhappiness()) {
              bestSort = sort;
              bestAverageUnhappinessOverIterations = averageUnhappinessOverIterations;
          }
      }

      int number;
      boolean go = false;
      for (Location i : bestSort.getLocations()) {
        String name1 = i.getName();
        number = 0;
        go = true;
        while (go) {
            if (number != 0)
                i.setName(name1 + " " + number);
            boolean found = false;
            for (Location ii : bestSort.getLocations())
                if (i.getName().equals(ii.getName()) && i != ii)
                    found = true;
            if (found) {
                number++;
            } else
            go = false;
        }
      }

      Gson gson = new Gson();

      Map<String, Object> result = new LinkedHashMap<>();
      result.put("title", sortInput.getString());
      result.put("numberOfSorts", sortInput.getNumber3());
      result.put("averageUnhappiness", bestSort.getUnhappiness()/bestSort.getIndividuals().length);
      result.put("averageUnhappinessOverIterations", bestAverageUnhappinessOverIterations);

      List<String> locationNames = new ArrayList<>();
      for (Location i : bestSort.getLocations()) {
          locationNames.add(i.getName());
      }
      List<List<String>> output = new ArrayList<>();
      output.add(locationNames);

      int max = 0;
      for (Location i : bestSort.getLocations())
          if (i.getMembers().size() > max)
              max = i.getMembers().size();

      for (int g = 0; g < max; g++) {
          List<String> row = new ArrayList<>();
          for (Location ii : bestSort.getLocations()) {
              if (ii.getMembers().size() > g) {
                  row.add(ii.getMembers().get(g).getName());
              } else {
                  row.add("");
              }
          }
          output.add(row);
      }
      result.put("output", output);

      String[] alpha = new String[bestSort.getIndividuals().length];
      int counter = 0;
      for (Location i : bestSort.getLocations()) {
          for (Individual ii : i.getMembers()) {
              alpha[counter] = ii.getName() + "sl1tter7139203945" + i.getName();
              counter++;
          }
      }

      Arrays.sort(alpha, Comparator.naturalOrder());

      List<List<String>> output1 = new ArrayList<>();

      // Populate the subsequent rows with data from the sorted alpha array
      for (String i : alpha) {
          List<String> row = new ArrayList<>();
          String[] parts = i.split("sl1tter7139203945");
          row.add(parts[0]); // Individual's name
          row.add(parts[1]); // Location
          output1.add(row);
      }
      result.put("output1", output1);

      String jsonString = gson.toJson(result);

      if (type.equals("normal")) {
          AccountFunctions.incrementSortCount(sortInput.getEmail(), 0);
          System.out.println("Sort Completed: title=" + sortInput.getString() + " iterations=" + sortInput.getNumber2() + " number of sorts=" + sortInput.getNumber3());
      } else {
        AccountFunctions.incrementSortCount(sortInput.getEmail(), 1);
        System.out.println("Random Sort Completed: title=" + sortInput.getString() + " number of sorts=" + sortInput.getNumber3());
      }

      return jsonString;
  }

  // TODO: protect with account
  @CrossOrigin(origins = "http://localhost:8888")
  @PostMapping("/uploadClean")
  public String handleFormUploadClean(@ModelAttribute CleanInput cleanInput) {
      
      ArrayList<ArrayList<String>> clean = MyUtility.readJsonStringInput(cleanInput.getClean());
      ArrayList<ArrayList<String>> toClean = MyUtility.readJsonStringInput(cleanInput.getToClean());
      Cleaner cleaner = new Cleaner(clean, toClean, cleanInput.getToCleanColumn());
      cleaner.cleanInput();

      Gson gson = new Gson();
      Map<String, Object> result = new LinkedHashMap<>();
      List<List<String>> output = new ArrayList<>();
      
      for (ArrayList<String> row : toClean) {
        List<String> outputRow = new ArrayList<>();
        for (String cell : row) {
            outputRow.add(cell);
        }
        output.add(outputRow);
      }

      result.put("output", output);

      String jsonString = gson.toJson(result);

      System.out.println("Clean Completed");

      return jsonString;
  }

  @CrossOrigin(origins = "http://localhost:8888")
  @PostMapping("/createAccount")
  public String handleFormCreateAccount(@ModelAttribute CreateAccountInput createAccountInput) {
        String checks = AccountServices.checkUsernameAndPasswordCA(createAccountInput.getEmail(), createAccountInput.getPassword(), createAccountInput.getConfirmPassword());
        if (! checks.equals("VALID"))
            return "{\"text\":\"" + checks + "\"}";
        AccountServices.createAccount(createAccountInput.getEmail(), createAccountInput.getPassword());
        return "{\"text\":\"" + "Account created." + "\"}";
  }

  @CrossOrigin(origins = "http://localhost:8888")
  @PostMapping("/login")
  public String handleFormLogin(@ModelAttribute LoginInput loginInput) {
        if (! AccountServices.checkUsernameAndPasswordL(loginInput.getEmail(), loginInput.getPassword()))
            return "{\"text\":\"INVALID\"}";
        return "{\"text\":\"" + AccountServices.login(loginInput.getEmail()) + "\"}";
  }

  @CrossOrigin(origins = "http://localhost:8888")
  @PostMapping("/verifyLogin")
  public String handelVerifyLogin(@ModelAttribute VerifyLoginInput verifyLoginInput) {
        if (! AccountServices.verifyLogin(verifyLoginInput.getEmail(), verifyLoginInput.getKey()))
            return "{\"text\":\"INVALID\"}";
        return "{\"text\":\"VALID\"}";
  }

  @CrossOrigin(origins = "http://localhost:8888")
  @PostMapping("/signout")
  public void handelSignout(@ModelAttribute SignoutInput signoutInput) {
        if (! AccountServices.verifyLogin(signoutInput.getEmail(), signoutInput.getKey()))
            return;
        AccountServices.signout(signoutInput.getEmail());
  }

  @CrossOrigin(origins = "http://localhost:8888")
  @PostMapping("/deleteAccount")
  public String handelDeleteAccount(@ModelAttribute DeleteAccountInput deleteAccountInput) {
        if (! AccountServices.verifyLogin(deleteAccountInput.getEmail(), deleteAccountInput.getKey()))
            return "{\"text\":\"INVALID\"}";
        AccountServices.deleteAccount(deleteAccountInput.getEmail());
        return "{\"text\":\"VALID\"}";
  }

  @CrossOrigin(origins = "http://localhost:8888")
  @PostMapping("/createForm")
  public String handelCreateForm(@ModelAttribute CreateFormInput createFormInput) {
        if (! AccountServices.verifyLogin(createFormInput.getEmail(), createFormInput.getKey()))
            return "{\"text\":\"INVALID\"}";
        if(Forms.doesFormExist(createFormInput.getEmail(), createFormInput.getFormName()))
            return "{\"text\":\"You have already created a form with that name.\"}";
        Forms.createForm(createFormInput.getEmail(), createFormInput.getFormName(), createFormInput.getSortTypes(), createFormInput.getOptions(), createFormInput.getTexts(), createFormInput.getIdInstruct(), createFormInput.getInputTypes());
        return "{\"text\":\"Form created.\"}";
  }

  @CrossOrigin(origins = "http://localhost:8888")
  @PostMapping("/getMyForms")
  public String handelGetMyForms(@ModelAttribute GetMyFormsInput getMyFormsInput) {
        System.out.println("HELLLO!!!!!");
        if (! AccountServices.verifyLogin(getMyFormsInput.getEmail(), getMyFormsInput.getKey()))
            return "{\"text\":\"INVALID\"}";
        System.out.println("HELLLO!!!!!1");
        String[] myForms = Forms.getUserFormNames(getMyFormsInput.getEmail());
        System.out.println("HELLLO!!!!!2");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("myForms", gson.toJsonTree(myForms));
        return jsonObject.toString();
  }

//   @CrossOrigin(origins = "http://localhost:8888")
//   @PostMapping("/deleteForm")
//   public String handelDeleteForm(@ModelAttribute DeleteFormInput deleteFormInput) {
//         if (! AccountServices.verifyLogin(deleteFormInput.getEmail(), deleteFormInput.getKey()))
//             return "{\"text\":\"INVALID\"}";
//         Forms.deleteForm(deleteFormInput.getEmail(), deleteFormInput.getFormName());
//         return "{\"text\":\"VALID\"}";
//   }

}