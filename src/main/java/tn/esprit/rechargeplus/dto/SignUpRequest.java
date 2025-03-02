package tn.esprit.rechargeplus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String email;
    private String password ;
    private String name ;
    private String address;
    private String mobileNumber;
    private Date birthDate;
}
