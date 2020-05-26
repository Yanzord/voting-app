package com.yanzord.votingappservice.feign;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
public interface CPFValidator {
    @RequestMapping(value = "/users/{cpf}", method = RequestMethod.GET)
    ObjectNode validateCPF(@PathVariable("cpf") String cpf);
}
