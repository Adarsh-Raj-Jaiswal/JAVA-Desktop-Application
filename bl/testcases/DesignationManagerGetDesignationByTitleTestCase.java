import com.thinking.machines.hr.bl.interfaces.managers.*;
import com.thinking.machines.hr.bl.interfaces.pojo.*;
import com.thinking.machines.hr.bl.exceptions.*;
import com.thinking.machines.hr.bl.pojo.*;
import com.thinking.machines.hr.bl.managers.*;
import java.util.*;

class DesignationManagerGetDesignationByTitleTestCase {
    public static void main(String gg[]) {
        String title = gg[0];
        try {
            DesignationManagerInterface designationManager;
            designationManager = DesignationManager.getDesignationManager();
            DesignationInterface designation;
            designation = designationManager.getDesignationByTitle(title);
            System.out.printf("Code %d, Title %s", designation.getCode(), designation.getTitle());
        } catch (BLException blException) {
            List<String> properties = blException.getProperties();
            properties.forEach((property) -> {
                System.out.println(blException.getException(property));
            });
        }
    }
}