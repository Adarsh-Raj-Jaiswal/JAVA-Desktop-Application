import com.thinking.machines.hr.dl.exceptions.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.hr.dl.dao.*;
import com.thinking.machines.hr.dl.dto.*;

public class DesignationTitleExistsTestCase {
    public static void main(String gg[]) {
        String title = gg[0];
        try {
            DesignationDAOInterface designationDAO;
            designationDAO = new DesignationDAO();
            System.out.println("Code exists: " + designationDAO.titleExists(title));
        } catch (DAOException daoException) {
            System.out.println(daoException.getMessage());
        }
    }
}