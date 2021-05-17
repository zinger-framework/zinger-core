class Api::SessionController < ApiController
  def index
    current_session = { 'token' => CustomerSession.extract_token(request.headers['Authorization']) }
    sessions = Customer.current.customer_sessions.sort_by { |session| session['token'] == current_session['token'] ? 0 : 1 }
      .map { |session| session.as_json('ui_profile', current_session) }
    render status: 200, json: { success: true, message: 'success', data: sessions }
  end

  def destroy
    session = Customer.current.customer_sessions.find_by_token(params['id'])
    if session.blank?
      render status: 400, json: { success: false, message: I18n.t('validation.invalid', param: 'session token') }
      return
    end

    if params['id'] == CustomerSession.extract_token(request.headers['Authorization'])
      render status: 400, json: { success: false, message: I18n.t('session.delete_failed') }
      return
    end

    session.destroy!
    render status: 200, json: { success: true, message: I18n.t('session.delete_success') }
  end
end
