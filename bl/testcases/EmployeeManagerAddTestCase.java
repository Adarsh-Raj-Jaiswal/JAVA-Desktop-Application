import com.thinking.machines.hr.bl.managers.*;
import com.thinking.machines.enums.*;
import com.thinking.machines.hr.bl.interfaces.managers.*;
import com.thinking.machines.hr.bl.interfaces.pojo.*;
import com.thinking.machines.hr.bl.pojo.*;
import com.thinking.machines.hr.bl.exceptions.*;
import java.util.*;
import java.text.*;
import java.math.*;

class EmployeeManagerAddTestCase {
    public static void main(String gg[]) {
        try {
            // String employeeId="";
            String name = "Heera Gautam";
            DesignationInterface designation = new Designation();
            designation.setCode(4);
            Date dateOfBirth = new Date();
            boolean isIndian = true;
            BigDecimal basicSalary = new BigDecimal("430000");
            String panNumber = "A123463";
            String aadharCardNumber = "U123463";
            EmployeeInterface employee = new Employee();
            employee.setName(name);
            employee.setDesignation(designation);
            employee.setDateOfBirth(dateOfBirth);
            employee.setGender(GENDER.MALE);
            employee.setIsIndian(isIndian);
            employee.setBasicSalary(basicSalary);
            employee.setPANNumber(panNumber);
            employee.setAadharCardNumber(aadharCardNumber);
            EmployeeManagerInterface employeeManager;
            employeeManager = EmployeeManager.getEmployeeManager();
            employeeManager.addEmployee(employee);
            System.out.printf("Employee added with employee Id. %s", employee.getEmployeeId());
        } catch (BLException blException) {
            if (blException.hasGenericException()) {
                System.out.println(blException.getGenericException());
            }
            List<String> properties = blException.getProperties();
            for (String property : properties) {
                System.out.println(blException.getException(property));
            }
        }

    }
}