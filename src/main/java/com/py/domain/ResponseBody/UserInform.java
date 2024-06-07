package com.py.domain.ResponseBody;

import com.py.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInform {
    private User user;
    private Integer fanNum;
    private Integer subscribeNum;
}
