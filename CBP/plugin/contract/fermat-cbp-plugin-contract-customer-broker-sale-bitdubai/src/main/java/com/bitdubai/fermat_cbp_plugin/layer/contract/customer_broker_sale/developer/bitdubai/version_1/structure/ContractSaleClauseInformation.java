package com.bitdubai.fermat_cbp_plugin.layer.contract.customer_broker_sale.developer.bitdubai.version_1.structure;

import com.bitdubai.fermat_cbp_api.all_definition.contract.ContractClause;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractClauseStatus;
import com.bitdubai.fermat_cbp_api.all_definition.enums.ContractClauseType;

import java.util.UUID;

/**
 * Created by angel on 28/11/15.
 */

public class ContractSaleClauseInformation implements ContractClause {

    // TODO: Cambiar los numeros primos
    private static final int HASH_PRIME_NUMBER_PRODUCT = 1117;
    private static final int HASH_PRIME_NUMBER_ADD = 3001;

    private final UUID clauseID;
    private final ContractClauseType type;
    private final Integer executionOrder;
    private final ContractClauseStatus status;

    public ContractSaleClauseInformation(
            UUID clauseID,
            ContractClauseType type,
            Integer executionOrder,
            ContractClauseStatus status
    ){
        this.clauseID = clauseID;
        this.type = type;
        this.executionOrder = executionOrder;
        this.status = status;
    }

    @Override
    public UUID getClauseId() {
        return this.clauseID;
    }

    @Override
    public ContractClauseType getType() {
        return this.type;
    }

    @Override
    public Integer getExecutionOrder() {
        return this.executionOrder;
    }

    @Override
    public ContractClauseStatus getStatus() {
        return this.status;
    }

    @Override
    public boolean equals(final Object o){
        if(!(o instanceof ContractClause))
            return false;
        ContractSaleClauseInformation compare = (ContractSaleClauseInformation) o;

        if(!this.clauseID.equals(compare.getClauseId()))
            return false;
        if(!this.type.equals(compare.getType()))
            return false;
        if(!this.executionOrder.equals(compare.getExecutionOrder()))
            return false;
        if(this.status != compare.getStatus())
            return false;

        return true;
    }

    @Override
    public int hashCode(){
        int c = 0;
        c += clauseID.hashCode();
        c += type.hashCode();
        c += executionOrder.hashCode();
        c += status.hashCode();
        return 	HASH_PRIME_NUMBER_PRODUCT * HASH_PRIME_NUMBER_ADD + c;
    }
}
