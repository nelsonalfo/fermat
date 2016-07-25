package com.bitdubai.fermat_api.layer.all_definition.navigation_structure;


import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.interfaces.FermatFragment;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.interfaces.FermatTab;

import java.io.Serializable;


/**
 * Created by Furszyfer Matias on 2015.07.17..
 */
public class Tab implements FermatTab, Serializable {

    /**
     * Tab class member variables
     */
    private String label;

    private byte[] icon;

    private FermatFragment fragment;

    private FermatDrawable drawable;

    /**
     * Custom View for the tab
     * eg: you can use frameworks view or your custom view
     */
    private FermatView fermatView;

    /**
     * Tab class constructors
     */
    public Tab() {
    }

    public Tab(String label, FermatRuntimeFragment fragment) {
        this.label = label;
        this.fragment = fragment;
    }

    /**
     * Tab class getters
     */

    public String getLabel() {
        return this.label;
    }

    @Override
    public void setFragment(FermatFragment fragment) {
        this.fragment = fragment;

    }


    public FermatFragment getFragment() {
        return fragment;
    }

    public byte[] getIcon() {
        return icon;
    }

    /**
     * Tab class setters
     */
    public void setLabel(String texto) {
        this.label = texto;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public FermatDrawable getDrawable() {
        return drawable;
    }

    public FermatView getFermatView() {
        return fermatView;
    }

    public void setFermatView(FermatView fermatView) {
        this.fermatView = fermatView;
    }

    public boolean hasCustomView() {
        return fermatView != null;
    }

    @Override
    public void setFermatDrawable(FermatDrawable fermatDrawable) {
        this.drawable = fermatDrawable;
    }

}
