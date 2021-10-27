package com.example.progettoium.ui.home.courses;

import android.content.Context;

import com.example.progettoium.data.CoursesTimeTable;

import java.util.ArrayList;
import java.util.List;

public class CoursesTimeTableRepository {

    private Context context;

    private static final String TAG = "CoursesTimeTableRepository";

    public CoursesTimeTableRepository(Context context) {
        this.context = context;
    }

    public List<CoursesTimeTable> fetchCourses() {
        /*per ora in locale*/
        ArrayList<CoursesTimeTable> corsi = new ArrayList<CoursesTimeTable>();
        corsi = CoursesTimeTable.createCourses();
        /*CoursesCustomAdapter adapter = new CoursesCustomAdapter(view.getContext(), R.layout.course_list_item, corsi);
        binding.coursesList.setAdapter(adapter);*/

        /*
        ArrayList<CoursesTimeTable> allCourses = null;

        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            allCourses = new ArrayList<CoursesTimeTable>();
            do {
                //Create a plain class with following variables - id, name, contactNumber, email
                CoursesTimeTable objContactDO = new CoursesTimeTable();

                objContactDO.name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
                while (emails.moveToNext()) {
                    objContactDO.email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    break;
                }
                emails.close();


                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        objContactDO.phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        break;
                    }
                    pCur.close();
                }

                alContacts.add(objContactDO);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return alContacts;

         */
        return corsi;
    }
}