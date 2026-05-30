package com.github.jukkarol.tests.creditService.atmService;

import com.github.jukkarol.clients.authService.AuthApiClient;
import com.github.jukkarol.clients.creditService.CreditApiClient;
import com.github.jukkarol.clients.creditService.CreditHistoryApiClient;
import com.github.jukkarol.clients.creditService.ToolsApiClient;
import com.github.jukkarol.clients.transactionService.AccountApiClient;
import com.github.jukkarol.clients.transactionService.TransactionApiClient;
import com.github.jukkarol.helpers.authService.RoleDbHelper;
import com.github.jukkarol.helpers.authService.UserDbHelper;
import com.github.jukkarol.helpers.creditService.transactionService.CreditDbHelper;
import com.github.jukkarol.helpers.transactionService.AccountDbHelper;
import com.github.jukkarol.model.Credit;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CreditHistoryTests {

    @Autowired
    private UserDbHelper userDbHelper;

    @Autowired
    private RoleDbHelper authDbHelper;

    @Autowired
    private AccountDbHelper accountDbHelper;

    @Autowired
    private CreditDbHelper creditDbHelper;

    @Autowired
    private AuthApiClient authApiClient;

    @Autowired
    private AccountApiClient accountApiClient;

    @Autowired
    private CreditApiClient creditApiClient;

    @Autowired
    private CreditHistoryApiClient creditHistoryApiClient;

    @Autowired
    private ToolsApiClient toolsApiClient;

    String randomUUID1 = UUID.randomUUID().toString().substring(10);
    String email= randomUUID1 + "@gmail.com";
    String name = randomUUID1;
    String password = "password!123";

    String randomUUID2 = UUID.randomUUID().toString().substring(10);
    String emailEmployee = randomUUID2 + "@gmail.com";
    String nameEmployee = randomUUID2;
    String passwordEmployee = "password!123";

    private String userToken, employeeToken;
    private String accountNumber;
    private String balance;

    BigDecimal transferAmount = new BigDecimal("100.12");

    @BeforeEach
    void setUp() throws Exception {
        authApiClient.signup(email, name, password);
        authApiClient.signup(emailEmployee, nameEmployee, passwordEmployee);

        authDbHelper.addRoleToUser(emailEmployee, "Employee");
        authDbHelper.addRoleToUser(emailEmployee, "Admin");

        userToken = authApiClient.login(email, password).jsonPath().getString("token");
        employeeToken = authApiClient.login(emailEmployee, passwordEmployee).jsonPath().getString("token");

        var acc = accountApiClient.createAccount(userToken);
        accountNumber = acc.jsonPath().getString("accountNumber");
        balance = acc.jsonPath().getString("balance");
    }

    @AfterEach
    void tearDown() throws Exception {
        userDbHelper.removeUser(email);
        userDbHelper.removeUser(emailEmployee);
        accountDbHelper.removeAccount(accountNumber);
        creditDbHelper.deleteCreditsByAccountNumber(accountNumber);
    }

    @Test
    void shouldCreateAndGetCreditWithThreeInstallment() throws Exception{
        //create credit
        int InstallmentTotal = 3;
        BigDecimal amountTotal = transferAmount.multiply(new BigDecimal(InstallmentTotal));
        Response responseCreateCredit = creditApiClient.createCredit(employeeToken, accountNumber, amountTotal, transferAmount);
        assertThat(responseCreateCredit.statusCode()).isIn(200);

        //delete to chyba
        List<Long> ids = new ArrayList<>();
        ids.add(responseCreateCredit.jsonPath().getLong("id"));
        assertThat(responseCreateCredit.jsonPath().getString("installmentTotal")).isEqualTo(String.valueOf(InstallmentTotal));
        assertThat(responseCreateCredit.jsonPath().getString("installmentLeft")).isEqualTo(String.valueOf(InstallmentTotal));

        //trigger credit iteration
        Response processInstallmentsResponse = toolsApiClient.processSpecifiedCreditsInstallments(employeeToken, ids);
        assertThat(processInstallmentsResponse.statusCode()).isIn(200);

        //wait for kafka process
        Thread.sleep(5000);

        //trigger credit iteration
        Response processInstallmentsResponse2 = toolsApiClient.processSpecifiedCreditsInstallments(employeeToken, ids);
        assertThat(processInstallmentsResponse2.statusCode()).isIn(200);

        Response processInstallmentsResponse3 = toolsApiClient.processSpecifiedCreditsInstallments(employeeToken, ids);
        assertThat(processInstallmentsResponse3.statusCode()).isIn(200);

        Response processInstallmentsResponse4 = toolsApiClient.processSpecifiedCreditsInstallments(employeeToken, ids);
        assertThat(processInstallmentsResponse4.statusCode()).isIn(404);

        Response responseGetCreditsByAccountNumber = creditApiClient.getCreditsByAccountNumber(employeeToken, accountNumber);
        assertThat(responseGetCreditsByAccountNumber.statusCode()).isIn(200);
        assertThat(responseGetCreditsByAccountNumber.jsonPath().getString("credits.accountNumber")).isEqualTo("[" + accountNumber + "]");
        assertThat(responseGetCreditsByAccountNumber.jsonPath().getString("credits.amountMonthly")).isEqualTo("[" + transferAmount.toString() + "]");
        assertThat(responseGetCreditsByAccountNumber.jsonPath().getString("credits.amountTotal")).isEqualTo("[" + amountTotal.toString() + "]");
        String creditId = responseGetCreditsByAccountNumber.jsonPath().getString("credits.id").replace("[", "").replace("]", "");

        Response responseGetCreditHistoryByCreditId = creditHistoryApiClient.getCreditHistoryByCreditId(employeeToken, Long.valueOf(creditId));
        assertThat(responseGetCreditHistoryByCreditId.statusCode()).isIn(200);
        responseGetCreditHistoryByCreditId.prettyPrint();

        assertThat(responseGetCreditHistoryByCreditId.jsonPath().getString("history.amountLeft")).isEqualTo("[" + transferAmount.multiply(new BigDecimal(2)) + ", " + transferAmount + ", 0.0]");
        assertThat(responseGetCreditHistoryByCreditId.jsonPath().getString("history.amount")).isEqualTo("[" + transferAmount + ", " + transferAmount + ", " + transferAmount + "]");
        assertThat(responseGetCreditHistoryByCreditId.jsonPath().getString("history.installmentLeft")).isEqualTo("[2, 1, 0]");
    }
}
