package creator.soft.cygi.com.friendlyloseweighthelper;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import creator.soft.cygi.com.friendlyloseweighthelper.utility.DateTimeStringUtility;

/**
 * Created by CygiMasterProgrammer on 2016-01-25.
 */
public class TestingBook {

    @Test @Ignore
    public void equalsAndHashCodeTesting()  {

        FewHasCodeTestClass fewHasCodeTest = new FewHasCodeTestClass();
      //  fewHasCodeTest.setName("jacek");
        FewHasCodeTestClass fewHasCodeTest2 = new FewHasCodeTestClass();

    //    fewHasCodeTest2 = fewHasCodeTest;


        System.out.println("First: "+ fewHasCodeTest.hashCode());

        System.out.println("Second: " + fewHasCodeTest2.hashCode());


        boolean isObjectEquals = fewHasCodeTest.equals(fewHasCodeTest2);

        boolean isReferencesEquals = fewHasCodeTest == fewHasCodeTest2;

        System.out.println("is references same: " + isReferencesEquals);
        System.out.println("is Object Equals Equal Method: " + isObjectEquals);

     //   assertTrue(fewHasCodeTest == fewHasCodeTest2);
        assertEquals(fewHasCodeTest.hashCode(), fewHasCodeTest2.hashCode());
        assertEquals(fewHasCodeTest, fewHasCodeTest2);
    }

     @Test @Ignore
     public  void generatedDateShow () {

        System.out.println(DateTimeStringUtility.getCurrentDate());

     }

    private class FewHasCodeTestClass {

        int number = 1;
        String name = "";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FewHasCodeTestClass that = (FewHasCodeTestClass) o;

            if (number != that.number) return false;
            return name.equals(that.name);

        }

        @Override
        public int hashCode() {
            int result = number;
            result = 31 * result + name.hashCode();
            return result;
        }
    }

}