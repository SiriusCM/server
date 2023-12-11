package org.sirius.server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.SQLException;

/**
 * @Date:2022/7/27
 * @Author:高连棣
 */
@ControllerAdvice(basePackages = "org.sirius.server")
public class PlanExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public ResponseEntity<?> handler(HttpClientErrorException e) {
        e.printStackTrace();
        return new ResponseEntity<>(e.getStatusCode());
    }

    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public ResponseEntity<?> handler(SQLException e) {
        e.printStackTrace();
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
