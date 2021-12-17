package vu.pham.todolistvu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.util.List;

public class ToDoListAdapter extends BaseAdapter {
    private ToDoListActivity context;
    private int layout;
    private List<ToDoModel>toDoModelList;

    public ToDoListAdapter(ToDoListActivity context, int layout, List<ToDoModel> toDoModelList) {
        this.context = context;
        this.layout = layout;
        this.toDoModelList = toDoModelList;
    }

    @Override
    public int getCount() {
        return toDoModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        CheckBox cbTrangthai;
        TextView txtNoiDung;
        ImageButton imgbtnEdit, imgbtnDelete;
        CardView cardView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(layout, null);
            holder=new ViewHolder();
            holder.cbTrangthai=(CheckBox) convertView.findViewById(R.id.checkboxToDoListItem);
            holder.txtNoiDung=(TextView) convertView.findViewById(R.id.textviewToDoListItem);
            holder.imgbtnEdit=(ImageButton) convertView.findViewById(R.id.imagebuttonEditItem);
            holder.imgbtnDelete=(ImageButton) convertView.findViewById(R.id.imagebuttonDeleteItem);
            holder.cardView=(CardView)convertView.findViewById(R.id.cardviewToDoListItem);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        ToDoModel toDoModel=toDoModelList.get(position);
        holder.cbTrangthai.setChecked(toDoModel.isTrangThai());
        holder.txtNoiDung.setText(toDoModel.getNoiDung());
        if(toDoModel.getMau() % 2==0){
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.purple_custom));
        }else{
            holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.pink));
        }
        holder.imgbtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.editToDoList(toDoModel, position);
            }
        });
        holder.imgbtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.deleteTodoList(position);
            }
        });
        holder.cbTrangthai.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    toDoModel.setTrangThai(true);
                }else{
                    toDoModel.setTrangThai(false);
                }
                context.updateStateTodolist(toDoModel);
            }
        });
        return convertView;
    }
}
