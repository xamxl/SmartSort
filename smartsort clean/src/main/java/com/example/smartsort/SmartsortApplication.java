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
import com.google.gson.GsonBuilder;
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
				registry.addMapping("/uploadTest-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/upload-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/uploadClean-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/createAccount-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/login-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/verifyLogin-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/signout-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/deleteAccount-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/createForm-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/getMyForms-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/deleteForm-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/getForm-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/submitForm-javaconfig").allowedOrigins("https://smartsort.site");
                registry.addMapping("/getSubmissions-javaconfig").allowedOrigins("https://smartsort.site");
			}
		};
	}

}

@RestController
class FormDataController {

  @CrossOrigin(origins = "https://smartsort.site")
  @GetMapping("/uploadTest")
  public String testUpload() {
    return "Upload connected.";
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/upload")
  public String handleFormUpload(@ModelAttribute SortInput sortInput, @RequestParam String type) {

      if (! AccountServices.verifyLogin(sortInput.getEmail(), sortInput.getKey()))
          return "{\"text\":\"INVALID\"}";

      Sort bestSort = null;
      int iterFound = -1;
      ArrayList<Double> bestAverageUnhappinessOverIterations = null;
      for (int sortNum = 0; sortNum < sortInput.getNumber3(); sortNum++) {
          double[][] weights = {{sortInput.getWeight1()}, {sortInput.getWeight2()}, {sortInput.getWeight3()}, {sortInput.getWeight4()}, {sortInput.getWeight5()}, {sortInput.getWeight6()}, {sortInput.getWeight7()}, {sortInput.getWeight8()}, sortInput.getWeight9(), sortInput.getWeight10(), {sortInput.getNumber1()}, sortInput.getWeight11(), sortInput.getWeight12()};
          ArrayList<ArrayList<String>> input = MyUtility.readJsonStringInput(sortInput.getFile1());
          Individual[] ind = new Individual[input.size() - 1];
          for (int i = 1; i < input.size(); i++)
              ind[i-1] = new Individual(input.get(i), input.get(0), new int[] {sortInput.getValue1(), sortInput.getValue2(), sortInput.getValue3(), sortInput.getValue4(), sortInput.getValue5(), sortInput.getValue6(), sortInput.getValue7(), sortInput.getValue8(), sortInput.getValue9(), sortInput.getValue10(), sortInput.getValue11(), sortInput.getValue12()}, weights);
          ArrayList<ArrayList<String>> input1 = MyUtility.readJsonStringInput(sortInput.getFile2());
          Location[] l = new Location[input1.size()];
          for (int i = 0; i < input1.size(); i++) {
              // SOLUTION: maybe needed above with individuals as well, if there is an error, skip that location
              try {
                l[i] = new Location(Integer.parseInt(input1.get(i).get(1)), input1.get(i).get(0), Integer.parseInt(input1.get(i).get(2)), weights);
              } catch (Exception e) {}
          }

          int nullCount = 0;
          for (Individual i : ind) {
          if (i.getName() == null)
              nullCount++;
          }

          Individual[] ind1 = new Individual[ind.length - nullCount];
          int counter = 0;
          for (Individual i : ind) {
            if (i.getName() != null) {
                ind1[counter] = i;
                counter++;
            }
          }
          ind = ind1;

          nullCount = 0;
          for (Location i : l) {
            if (i == null || i.getName() == null)
                nullCount++;
          }
        
          Location[] l1 = new Location[l.length - nullCount];
          counter = 0;
          for (Location i : l) {
            // problem??
              if (i != null && i.getName() != null) {
                  l1[counter] = i;
                  counter++;
              }
          }
          l = l1;

          Sort sort = new Sort(l, ind);
          for (int i = 0; i < l.length; i++)
              l[i].calculateMaxUnhappiness(sort);

          int iter = sortInput.getNumber2();
          if (iter > 1000000) {
              iter = 1000000;
          }
          ArrayList<Double> averageUnhappinessOverIterations = new ArrayList<>();

        int iS = -1;
        if (type.equals("normal")) {
              sort.allChoiceAssignment();

              double minTotalUnhappiness = sort.getUnhappiness();
              iS = 0;
              Sort minSort = sort.copy();

              // TODO: Confirm that iteration actually helps
              for (int i = 0; i < iter; i++) {
                  // TODO: What is the best setting for reassignX?
                  sort.reassignX(3);
                  sort.sumUnhappiness();
                  averageUnhappinessOverIterations.add(sort.getUnhappiness()/sort.getIndividuals().length);

                  if (sort.getUnhappiness() < minTotalUnhappiness) {
                      minTotalUnhappiness = sort.getUnhappiness();
                      minSort = sort.copy();
                      iS = i;
                  }
              }

              sort = minSort;
          } else {
              sort.randomAssignment();
              sort.sumUnhappiness();
          }
          if (bestSort == null || bestSort.getUnhappiness() > sort.getUnhappiness()) {
              bestSort = sort;
              bestAverageUnhappinessOverIterations = averageUnhappinessOverIterations;
              iterFound = iS;
          }
      }

      ArrayList<String> names = new ArrayList<>();
      ArrayList<Integer> numbers = new ArrayList<>();
      for (Location i : bestSort.getLocations()) {
          if (names.indexOf(i.getName()) == -1) {
                names.add(i.getName());
                numbers.add(1);
            } else {
                numbers.set(names.indexOf(i.getName()), numbers.get(names.indexOf(i.getName())) + 1);
            }
      }
      for (int i = 0; i < numbers.size(); i++) {
          if (numbers.get(i) == 1) {
              numbers.remove(i);
              names.remove(i);
              i--;
          }
      }
      for (Location i : bestSort.getLocations()) {
        if (names.indexOf(i.getName()) != -1 && numbers.get(names.indexOf(i.getName())) >= 1) {
            numbers.set(names.indexOf(i.getName()), numbers.get(names.indexOf(i.getName())) - 1);
            i.setName(i.getName() + " " + (numbers.get(names.indexOf(i.getName()))+1));
      }
    }

      for (Location i : bestSort.getLocations()) {
        for (Individual ii : i.getMembers()) {
            ii.setFalseChoiceUnhappy();
        }
      }
      bestSort.sumUnhappiness();

      Gson gson = new Gson();

      Map<String, Object> result = new LinkedHashMap<>();
      result.put("title", sortInput.getString());
      result.put("numberOfSorts", sortInput.getNumber3());
      result.put("averageUnhappiness", bestSort.getUnhappiness()/bestSort.getIndividuals().length);
      result.put("averageUnhappinessOverIterations", bestAverageUnhappinessOverIterations);
      result.put("iterFound", iterFound);

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

        List<List<String>> output1 = new ArrayList<>();

        // Populate the first row with the column headers
        List<String> header = new ArrayList<>();
        header.add("Name");
        header.add("Location");
        for (String h : bestSort.getIndividuals()[0].getHeaders()) {
            header.add(h);
        }
        output1.add(header);

        for (Location i : bestSort.getLocations()) {
            for (Individual ii : i.getMembers()) {
                List<String> row = new ArrayList<>();
                row.add(ii.getName()); // Individual's name
                row.add(i.getName()); // Location
                String[] choices = ii.getChoices();
                for (String choice : choices) {
                    row.add(choice);
                }
                output1.add(row);
            }
        }
        result.put("output1", output1);

        List<List<Boolean>> output2 = new ArrayList<>();

        for (Location i : bestSort.getLocations()) {
            for (Individual ii : i.getMembers()) {
                List<Boolean> row = new ArrayList<>();
                row.add(i.isUnhappy());
                boolean[] choices = ii.getChoiceUnhappy();
                for (boolean choice : choices) {
                    row.add(choice);
                }
                output2.add(row);
            }
        }
        result.put("output2", output2);

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
  @CrossOrigin(origins = "https://smartsort.site")
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

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/createAccount")
  public String handleFormCreateAccount(@ModelAttribute CreateAccountInput createAccountInput) {
        String checks = AccountServices.checkUsernameAndPasswordCA(createAccountInput.getEmail(), createAccountInput.getPassword(), createAccountInput.getConfirmPassword());
        if (! checks.equals("VALID"))
            return "{\"text\":\"" + checks + "\"}";
        AccountServices.createAccount(createAccountInput.getEmail(), createAccountInput.getPassword());
        return "{\"text\":\"" + "Account created." + "\"}";
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/login")
  public String handleFormLogin(@ModelAttribute LoginInput loginInput) {
        if (! AccountServices.checkUsernameAndPasswordL(loginInput.getEmail(), loginInput.getPassword()))
            return "{\"text\":\"INVALID\"}";
        return "{\"text\":\"" + AccountServices.login(loginInput.getEmail()) + "\"}";
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/verifyLogin")
  public String handelVerifyLogin(@ModelAttribute VerifyLoginInput verifyLoginInput) {
        if (! AccountServices.verifyLogin(verifyLoginInput.getEmail(), verifyLoginInput.getKey()))
            return "{\"text\":\"INVALID\"}";
        return "{\"text\":\"VALID\"}";
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/signout")
  public void handelSignout(@ModelAttribute SignoutInput signoutInput) {
        if (! AccountServices.verifyLogin(signoutInput.getEmail(), signoutInput.getKey()))
            return;
        AccountServices.signout(signoutInput.getEmail());
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/deleteAccount")
  public String handelDeleteAccount(@ModelAttribute DeleteAccountInput deleteAccountInput) {
        if (! AccountServices.verifyLogin(deleteAccountInput.getEmail(), deleteAccountInput.getKey()))
            return "{\"text\":\"INVALID\"}";
        AccountServices.deleteAccount(deleteAccountInput.getEmail());
        return "{\"text\":\"VALID\"}";
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/createForm")
  public String handelCreateForm(@ModelAttribute CreateFormInput createFormInput) {
        if (! AccountServices.verifyLogin(createFormInput.getEmail(), createFormInput.getKey()))
            return "{\"text\":\"INVALID\"}";
        if(Forms.doesFormExist(createFormInput.getEmail(), createFormInput.getFormName()))
            return "{\"text\":\"You have already created a form with that name.\"}";
        Forms.createForm(createFormInput.getEmail(), createFormInput.getFormName(), createFormInput.getSortTypes(), createFormInput.getOptions(), createFormInput.getTexts(), createFormInput.getIdInstruct(), createFormInput.getInputTypes());
        return "{\"text\":\"Form created.\"}";
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/getMyForms")
  public String handelGetMyForms(@ModelAttribute GetMyFormsInput getMyFormsInput) {
        if (! AccountServices.verifyLogin(getMyFormsInput.getEmail(), getMyFormsInput.getKey()))
            return "{\"text\":\"INVALID\"}";
        String[] myForms = Forms.getUserFormNames(getMyFormsInput.getEmail());
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("myForms", gson.toJsonTree(myForms));
        return jsonObject.toString();
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/deleteForm")
  public String handelDeleteForm(@ModelAttribute DeleteFormInput deleteFormInput) {
        if (! AccountServices.verifyLogin(deleteFormInput.getEmail(), deleteFormInput.getKey()))
            return "{\"text\":\"INVALID\"}";
        Forms.deleteForm(deleteFormInput.getEmail(), deleteFormInput.getFormName());
        return "{}";
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/getForm")
  public String handelGetForm(@ModelAttribute GetFormInput getFormInput) {
        if (! Forms.doesFormExist(getFormInput.getEmail(), getFormInput.getFormName()))
            return "{\"text\":\"INVALID\"}";
        Map<String, Object> formData = Forms.getForm(getFormInput.getEmail(), getFormInput.getFormName());
        Gson gson = new Gson();
        return gson.toJson(formData);
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/submitForm")
  public String handelSubmitForm(@ModelAttribute SubmitFormInput submitFormInput) {
    if (! Forms.validIdentifierAndForm(submitFormInput.getEmail(), submitFormInput.getFormName(), submitFormInput.getSubmissions()))
        return "{\"text\":\"INVALID_IDENTIFIER\"}";
    if (! Forms.validResponse(submitFormInput.getEmail(), submitFormInput.getFormName(), submitFormInput.getSubmissions()))
        return "{\"text\":\"INVALID_RESPONSE\"}";
    Forms.writeSubmission(submitFormInput.getEmail(), submitFormInput.getFormName(), submitFormInput.getSubmissions());
    return "{}";
  }

  @CrossOrigin(origins = "https://smartsort.site")
  @PostMapping("/getSubmissions")
  public String handelGetSubmissions(@ModelAttribute GetSubmissionsInput getSubmissionsInput) {
    if (! AccountServices.verifyLogin(getSubmissionsInput.getEmail(), getSubmissionsInput.getKey()))
            return "{\"text\":\"INVALID_LOGIN\"}";
    if (! Forms.doesFormExist(getSubmissionsInput.getEmail(), getSubmissionsInput.getFormName()))
            return "{\"text\":\"INVALID_FORM\"}";
    Map<String, Object>[] submissions = Forms.getSubmissions(getSubmissionsInput.getEmail(), getSubmissionsInput.getFormName());
    Gson gson = new GsonBuilder().create();
    return gson.toJson(submissions);
  }

}