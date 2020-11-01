class Admin::CustomerController < AdminController
  def index
    @title = 'Customers'
    if params['q'].present?
      @customers = if params['q'].match(Customer::EMAIL_REGEX)
        Customer.unscoped.where(email: params['q'])
      elsif params['q'].match(Customer::MOBILE_REGEX)
        Customer.unscoped.where(mobile: params['q'])
      else
        Customer.unscoped.where(id: params['q'])
      end
    end
  end

  def update
    customer = Customer.fetch_by_id(params['id'])
    customer.update(name: params['name'], status: params['status'])
    if customer.errors.any?
      flash[:error] = customer.errors.messages.values.flatten.first
    else
      flash[:success] = 'Update is successful'
    end
    
    redirect_to customer_index_path(q: params['id'])
  end

  def destroy
    customer = Customer.fetch_by_id(params['id'])
    if customer.present?
      customer.update!(deleted: true)
      flash[:success] = 'Deletion is successful'
    else
      flash[:error] = 'Deletion failed'
    end
    
    redirect_to customer_index_path(q: params['id'])
  end
end
