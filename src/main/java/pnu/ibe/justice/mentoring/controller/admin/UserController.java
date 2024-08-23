package pnu.ibe.justice.mentoring.controller.admin;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pnu.ibe.justice.mentoring.model.Role;
import pnu.ibe.justice.mentoring.model.UserDTO;
import pnu.ibe.justice.mentoring.service.UserService;
import pnu.ibe.justice.mentoring.util.ReferencedWarning;
import pnu.ibe.justice.mentoring.util.WebUtils;


@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("roleValues", Role.values());
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/user/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("user") final UserDTO userDTO) {
        return "admin/user/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("user") @Valid final UserDTO userDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/user/add";
        }
        userService.create(userDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.create.success"));
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId, final Model model) {
        model.addAttribute("user", userService.get(seqId));
        return "admin/user/edit";
    }

    @PostMapping("/edit/{seqId}")
    public String edit(@PathVariable(name = "seqId") final Integer seqId,
            @ModelAttribute("user") @Valid final UserDTO userDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/user/edit";
        }
        userService.update(seqId, userDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("user.update.success"));
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{seqId}")
    public String delete(@PathVariable(name = "seqId") final Integer seqId,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = userService.getReferencedWarning(seqId);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            userService.delete(seqId);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("user.delete.success"));
        }
        return "redirect:/admin/users";
    }

}
