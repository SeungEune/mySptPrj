package biz.llm.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AI 채팅 화면 Controller
 */
@Controller
@RequestMapping("/llm")
public class AiChatViewController {

    @Value("${local.llm.model:llama3}")
    private String modelName;

    @GetMapping("/chatForm.do")
    public String chatForm(Model model) {
        model.addAttribute("pageTitle", "AI 채팅");
        model.addAttribute("modelName", modelName);
        return "llm/chatForm";
    }

    @GetMapping("/langchainChatForm.do")
    public String langchainChatForm(Model model) {
        model.addAttribute("pageTitle", "LangChain 채팅");
        model.addAttribute("modelName", modelName);
        return "llm/langchainChatForm";
    }
}
