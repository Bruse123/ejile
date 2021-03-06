package com.qtztlink.ejile.system.model.user;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class Shop implements User, Serializable {
    private static final long serialVersionUID = -4407788892168886423L;

    public interface ShopView { }

    public interface AdminView extends ShopView { }

//    public interface RegisterView { }

    @JsonView(ShopView.class)
    private Integer id;

    @JsonView(ShopView.class)
    private String username;

    @JsonView(AdminView.class)
    private String password;

    @JsonView(ShopView.class)
    private String telephone;

    @JsonView(ShopView.class)
    private String address;

    @JsonView(ShopView.class)
    private String managerName;

    @JsonView(ShopView.class)
    private String managerEmail;

    @JsonView(ShopView.class)
    private String pictureUrl;

    @JsonView(ShopView.class)
    private Double score;
}
