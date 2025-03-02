package tn.esprit.rechargeplus.dto;

import lombok.Data;


@Data
public class ChangePasswordDto {
    Long idUser;
    String newPassword;
}
