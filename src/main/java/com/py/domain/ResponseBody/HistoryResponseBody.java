package com.py.domain.ResponseBody;

import com.py.domain.Game;
import com.py.domain.History;
import com.py.domain.RequestBody.AddHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponseBody {
    private Game game;
    private AddHistory history;
}
