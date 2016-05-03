package edu.rosehulman.rafinder.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import edu.rosehulman.rafinder.MainActivity;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.UserType;
import edu.rosehulman.rafinder.model.person.Employee;

/**
 * The Profile Page.
 */
@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment implements View.OnLongClickListener {
    private static final int SELECT_IMAGE_REQUEST_CODE = 1;
    private StudentProfileListener mListener;
    private Employee employee;
    private MainActivity mContext;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
        case R.id.phoneTextView:
        case R.id.statusDetailTextView:
            // intentional fallthrough
            showEditTextDialog((TextView) v);
            break;
        case R.id.statusTextView:
            showStatusChooserDialog((TextView) v);
            break;
        case R.id.profileImageView:
            showImageChooserDialog();
            break;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImageUri = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getActivity().getContentResolver().query(selectedImageUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                Bitmap bmp = BitmapFactory.decodeFile(picturePath);
                employee.setProfilePictureFromBitmap(bmp);
                ((ImageView) getActivity().findViewById(R.id.profileImageView))
                        .setImageBitmap(bmp);
                saveProfilePictureToDatabase();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (StudentProfileListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement StudentProfileListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mListener != null) {
            employee = mListener.getSelectedEmployee();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = (MainActivity) view.getContext();

        // Fetch data
        TextView nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        TextView roomTextView = (TextView) view.findViewById(R.id.roomTextView);
        TextView emailTextView = (TextView) view.findViewById(R.id.emailTextView);
        TextView phoneTextView = (TextView) view.findViewById(R.id.phoneTextView);
        TextView statusTextView = (TextView) view.findViewById(R.id.statusTextView);
        TextView statusDetailTextView = (TextView) view.findViewById(R.id.statusDetailTextView);
        ImageView imageView = (ImageView) view.findViewById(R.id.profileImageView);

        nameTextView.setText(employee.getName());
        roomTextView.setText(getString(R.string.profile_room_format, employee.getHall(), employee.getRoom()));
        emailTextView.setText(getString(R.string.profile_email_format, employee.getEmail()));
        phoneTextView.setText(getString(R.string.profile_phone_format, employee.getPhoneNumber()));
        statusTextView.setText(getString(R.string.status_format, employee.getStatus()));
        statusDetailTextView.setText(getString(R.string.status_detail_format, employee.getStatusDetail()));

        Bitmap profilePicture = employee.getProfilePictureAsBitmap();
        if (profilePicture != null) {
            imageView.setImageBitmap(profilePicture);
        } else {
            imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_person));
        }

        // Set editable fields based on MainActivity.getUserType
        if (!mContext.getUserType().equals(UserType.RESIDENT)
            && mContext.getUser() != null
            && employee.equals(mContext.getUser())) {

            phoneTextView.setOnLongClickListener(this);
            statusTextView.setOnLongClickListener(this);
            statusDetailTextView.setOnLongClickListener(this);
            imageView.setOnLongClickListener(this);
            showEditPromptDialog();
        }

        view.findViewById(R.id.phoneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.dialPhoneNumber(employee.getPhoneNumber());
            }
        });

        view.findViewById(R.id.emailButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.sendEmail(employee.getEmail());
            }
        });

        view.findViewById(R.id.feedbackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedbackDialog();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showEditPromptDialog() {
        // CONSIDER: only do this the first time an Employee uses this screen, like the NavDrawer?
        DialogFragment dialogFragment = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.edit_prompt));
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                return builder.create();
            }
        };
        dialogFragment.show(getFragmentManager(), "editPrompt");
    }

    private void showEditTextDialog(final TextView v) {
        DialogFragment dialogFragment = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_edit_text, null);
                final EditText editText = (EditText) view.findViewById(R.id.editText);
                setMessageAndType(builder, editText, v.getId());

                builder.setView(view);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setTextAndEmployeeField(v, editText.getText().toString());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                return builder.create();
            }
        };
        dialogFragment.show(getFragmentManager(), "editText");
    }

    private void setMessageAndType(AlertDialog.Builder builder, EditText editText, int id) {
        final String hint;
        switch (id) {
        case R.id.phoneTextView:
            hint = getString(R.string.profile_edit_phone);
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
            editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            break;
        case R.id.statusDetailTextView:
            hint = getString(R.string.profile_edit_status_detail);
            break;
        default:
            hint = "value";
            break;
        }
        final String message = getString(R.string.profile_edit_message_format, hint);
        builder.setTitle(message);
        editText.setHint(hint);
    }

    private void setTextAndEmployeeField(TextView textView, String value) {
        switch (textView.getId()) {
        case R.id.phoneTextView:
            textView.setText(getString(R.string.profile_phone_format, value));
            employee.setPhoneNumber(value);
            employee.getFirebase().child("phoneNumber").setValue(employee.getPhoneNumber());
            break;
        case R.id.statusDetailTextView:
            textView.setText(getString(R.string.status_detail_format, value));
            employee.setStatusDetail(value);
            employee.getFirebase().child("statusDetail").setValue(employee.getStatusDetail());
            break;
        }
    }

    private void showStatusChooserDialog(final TextView v) {
        DialogFragment dialogFragment = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.profile_edit_message_default));
                final String[] statuses = getResources().getStringArray(R.array.default_statuses);
                statuses[0] = String.format(statuses[0], employee.getHall());
                builder.setItems(statuses, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String status = statuses[which];
                        v.setText(getString(R.string.status_format, status));
                        employee.setStatus(status);
                        employee.getFirebase().child("status").setValue(employee.getStatus()); // async
                    }
                });
                return builder.create();
            }
        };
        dialogFragment.show(getFragmentManager(), "statusChooser");
    }

    private void showImageChooserDialog() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_IMAGE_REQUEST_CODE);
    }

    private void saveProfilePictureToDatabase() {
        String image = employee.getProfilePicture();
        employee.getFirebase().child("profilePicture").setValue(image);
    }

    private void showFeedbackDialog() {
        DialogFragment dialogFragment = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_edit_text, null);
                final EditText editText = (EditText) view.findViewById(R.id.editText);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                editText.setMinLines(5);

                final String hint = getString(R.string.profile_feedback);
                final String message = getString(R.string.profile_edit_message_format, hint);
                builder.setTitle(message);
                editText.setHint(hint);

                builder.setView(view);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.sendFeedback(employee.getName(), employee.getEmail(), editText.getText().toString());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                return builder.create();
            }
        };
        dialogFragment.show(getFragmentManager(), "feedback");
    }

    public interface StudentProfileListener {
        public Employee getSelectedEmployee();
        public void sendFeedback(String name, String email, String body);
    }
}
