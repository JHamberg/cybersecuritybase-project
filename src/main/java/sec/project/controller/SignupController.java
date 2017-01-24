package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.database.Database;
import sec.project.repository.SignupRepository;

import java.util.List;

@Controller
public class SignupController {
    private List<String> webUsers;
    private Database db = Database.getInstance();

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(Model model, @RequestParam String name, @RequestParam String reason) {
        // We do not use repository
        // signupRepository.save(new WebUser(name, address));
        List<String> list = db.fetch("SELECT * from WebUsers");
        db.execute("INSERT INTO WebUsers (name, reason) VALUES ('" + name  + "', '"+ reason +"')");
        model.addAttribute("list", list);
        model.addAttribute("latest", name);
        return "done";
    }

}
