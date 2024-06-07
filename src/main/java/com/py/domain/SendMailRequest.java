package com.py.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @Author PY
 * @Create 2023/11/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMailRequest {
    private String toUserMail;
    private String where;
}
