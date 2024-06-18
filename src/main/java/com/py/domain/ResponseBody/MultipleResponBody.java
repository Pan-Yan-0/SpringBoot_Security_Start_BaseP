package com.py.domain.ResponseBody;

import com.py.domain.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleResponBody {
    private Game game;
    private Long opposeId;
    private String opposeName;
    private String avatar;
}
