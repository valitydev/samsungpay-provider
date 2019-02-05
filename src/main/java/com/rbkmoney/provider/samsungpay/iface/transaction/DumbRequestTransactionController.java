package com.rbkmoney.provider.samsungpay.iface.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbkmoney.provider.samsungpay.service.SPayService;
import com.rbkmoney.woody.api.flow.error.WErrorType;
import com.rbkmoney.woody.api.flow.error.WRuntimeException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by vpankrashkin on 04.04.18.
 */

@RestController
@RequestMapping("/${server.rest.endpoint}")
@Api(description = "Transaction creation API")
public class DumbRequestTransactionController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SPayService service;

    private ObjectMapper mapper = new ObjectMapper();


    @ApiOperation(value = "Request SamsungPay transaction", notes = "")
    @PostMapping(value = "/transaction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, headers = "Content-Type=application/json")
    @ApiResponses(value = {
            @ApiResponse(code= 200, message = "Samsung Pay session object"),
            @ApiResponse(code = 500, message = "Internal service error"),
            @ApiResponse(code = 503, message = "Samsung Pay service unavailable")
    })
    @CrossOrigin

    public ResponseEntity<String> getTransaction(@RequestBody Map<String, Object> request) {
        log.info("New Transaction request: {}", request);

        try {
            return ResponseEntity.ok(service.createTransaction(mapper.writeValueAsString(request)));
        } catch (WRuntimeException e) {
            WErrorType errorType = e.getErrorDefinition().getErrorType();
            if (errorType == WErrorType.UNDEFINED_RESULT || errorType == WErrorType.UNAVAILABLE_RESULT) {
                log.warn("Samsung pay service unavailable", e);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Third party service unavailable");
            } else {
                log.error("Failed to request transaction", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to request transaction");
            }
        } catch (Exception e) {
            log.error("Failed to request transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to request transaction");
        }

    }

}
