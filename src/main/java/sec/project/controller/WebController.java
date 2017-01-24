package sec.project.controller;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerMapping;
import sec.project.Constants;
import sec.project.database.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {
    private Database db = Database.getInstance();

    @RequestMapping("/")
    public String defaultMapping() {
        return "redirect:/form";
    }

    // *** A10 Unvalidated Redirects and Forwards ***
    // Redirecting the user without a prompt exposes the feature to phishing attacks.
    // Easiest fix for this particular application is replacing the redirect with the
    // actual link since we aren't interested in outgoing traffic.

    // Fix: Remove this mapping and change the template form.html.
    @RequestMapping(value = "/redirect/**", method = RequestMethod.GET)
    public String redirect(HttpServletRequest request) {
        String url = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        url = url.replaceFirst("/redirect/", "");
        return "redirect:" + "http://"+url;
    }

    // *** A4 Insecure Direct Object References ***
    // This mapping allows direct access to restricted resources mainly because they
    // can be guessed due to the AUTO_INCREMENT strategy of the primary key.

    // Fix: Change to a long UUID and/or use session based authentication.
    @RequestMapping(value = "/appointment/{id}", method = RequestMethod.GET)
    public String loadAppointment(Model model, @PathVariable int id) throws SQLException {
        // Open database connection
        db.openConnection();

        // Fetch the user with the requested id
        ResultSet resultSet = db.getUser(id);
        resultSet.next();
        String appointmentId = Integer.toString(id);
        String name = resultSet.getString("name");
        String reason = resultSet.getString("reason");
        resultSet.close();

        // Close database connection
        db.closeConnection();

        // Pass data to view
        model.addAttribute("id", appointmentId);
        model.addAttribute("name", name);
        model.addAttribute("reason", reason);
        return "appointment";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/queue", method = RequestMethod.GET)
    public String getQueue(Model model) throws SQLException {
        db.openConnection();
        List<String> list = new ArrayList<>();

        // Fetch and format all past appointments
        ResultSet resultSet = db.getUsers();
        ResultSet resultSet = db.fetch("SELECT * from Users");
        if(resultSet == null){
            return "error";
        }
        int columns = resultSet.getMetaData().getColumnCount();
        while(resultSet.next()){
            StringBuilder properties = new StringBuilder();
            for(int i=1; i <= columns; i++){
                System.out.println("Column : " + i);
                if(!Constants.SENSITIVE_FIELD_IDS.contains(i)){
                    System.out.println("string: " + resultSet.getString(i));
                    properties.append("\t").append(resultSet.getString(i));
                }
            }
            list.add(properties.toString());
        }
        resultSet.close();

        // Close database connection
        db.closeConnection();

        // Pass data to view
        model.addAttribute("list", list);
        return "queue";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String reason) throws SQLException {
        // Open database connection
        db.openConnection();

        // Insert new appointment into database

        // *** A1 Injection ***
        // This insert sequence is unsafe as untrusted data is not separated from the command.
        // User can exploit this property by injecting his own SQL such as '); DROP TABLE Users;--.

        // Fix: Remove the following line and uncomment the line below it
        int primaryKey = db.insert("INSERT INTO WebUsers (name, reason) VALUES ('" + name  + "', '"+ reason +"')");
        //int primaryKey = db.safeInsert(name, reason);
        if(primaryKey == -1){
            return "error";
        }
        db.closeConnection();

        return "redirect:appointment/" + primaryKey;
    }

}
