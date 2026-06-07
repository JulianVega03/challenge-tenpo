package com.challenge.challengetenpo.infrastructure.entrypoints.apirest;

import com.challenge.challengetenpo.domain.model.CallHistory;
import com.challenge.challengetenpo.domain.model.PageQuery;
import com.challenge.challengetenpo.domain.model.PageResult;
import com.challenge.challengetenpo.domain.usecase.GetCallHistoryUseCase;
import com.challenge.challengetenpo.infrastructure.entrypoints.apirest.swagger.HistoryApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class HistoryController implements HistoryApi {

    private final GetCallHistoryUseCase getCallHistoryUseCase;

    @Override
    @GetMapping("/history")
    public ResponseEntity<PageResult<CallHistory>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(getCallHistoryUseCase.execute(new PageQuery(page, size)));
    }
}
